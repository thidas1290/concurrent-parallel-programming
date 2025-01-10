package org.concurrentprogramming.bankingSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TransactionSystem {
    private final List<BankAccount> accounts;

    // Thread-safe list of all transaction logs for auditing or rollback
    private final List<TransactionLogEntry> transactionLogs = Collections.synchronizedList(new ArrayList<>());

    public TransactionSystem(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    public void transfer(int fromId, int toId, double amount) throws InterruptedException {
        if (fromId == toId)
        {
            System.out.println("Cannot transfer to the same account.");
            return;
        }

        TransactionLogEntry logEntry = new TransactionLogEntry(fromId, toId, amount);
        transactionLogs.add(logEntry);

        BankAccount fromAccount = getAccount(fromId);
        BankAccount toAccount   = getAccount(toId);

        // Order locks by ascending ID to avoid deadlock
        // 1 2
        // 2 1
        BankAccount firstLock  = (fromAccount.getAccountId() < toAccount.getAccountId()) ? fromAccount : toAccount;
        BankAccount secondLock = (firstLock == fromAccount) ? toAccount : fromAccount;

        firstLock.writeLock();
        secondLock.writeLock();

        try {
            // Check sufficient funds
            if (fromAccount.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient funds in Account #" + fromId);
            }

            // Withdraw from source
            fromAccount.withdrawUnsafe(amount);
            logEntry.setWithdrawnFromSource(true);

            // Deposit to target
            toAccount.depositUnsafe(amount);
            logEntry.setDepositedToTarget(true);

            logEntry.markCompletedSuccessfully();
            System.out.println("Transferred $" + amount + " from Account#" + fromId
                    + " to Account#" + toId + " [TX-ID: " + logEntry.getTransactionId() + "]");
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage() + " [TX-ID: " + logEntry.getTransactionId() + "]");
            rollback(logEntry);
        } finally {
            secondLock.writeUnlock();
            firstLock.writeUnlock();                  
        }
    }

    /**
     * A more detailed rollback using TransactionLogEntry
     * so we revert only the parts that were completed.
     */
    private void rollback(TransactionLogEntry logEntry)
    {
        int fromId = logEntry.getFromAccountId();
        int toId   = logEntry.getToAccountId();
        double amount = logEntry.getAmount();

        BankAccount fromAccount = getAccount(fromId);
        BankAccount toAccount   = getAccount(toId);

        System.out.println("Rolling back transaction: " + logEntry.toString());

        // Lock both accounts again in ascending order for rollback
        BankAccount firstLock  = (fromAccount.getAccountId() < toAccount.getAccountId()) ? fromAccount : toAccount;
        BankAccount secondLock = (firstLock == fromAccount) ? toAccount : fromAccount;

        firstLock.writeLock();
        secondLock.writeLock();
        try {
            // If we withdrew from source but did NOT deposit to target,
            // just deposit back to the source
            if (logEntry.isWithdrawnFromSource() && !logEntry.isDepositedToTarget())
            {
                fromAccount.depositUnsafe(amount);
                System.out.println("[Rollback] Re-deposited $" + amount + " to Account#" + fromId);
            }
            // If we withdrew from source AND deposited to target,
            // we must withdraw from target and redeposit into source
            else if (logEntry.isWithdrawnFromSource() && logEntry.isDepositedToTarget())
            {
                if (toAccount.getBalance() >= amount)
                {
                    toAccount.withdrawUnsafe(amount);
                    System.out.println("[Rollback] Withdrew $" + amount + " from Account#" + toId);
                } else {
                    // If for some reason the target is short now, we still withdraw for consistency
                    toAccount.withdrawUnsafe(amount);
                    System.err.println("[Rollback] Target was short, forcibly withdrew $" + amount + " from Account#" + toId);
                }
                fromAccount.depositUnsafe(amount);
                System.out.println("[Rollback] Re-deposited $" + amount + " to Account#" + fromId);
            }
        } finally {
            secondLock.writeUnlock();
            firstLock.writeUnlock();
        }
    }

    public double readBalance(int accountId)
    {
        return getAccount(accountId).getBalance();
    }

    public List<String> readTransactionHistory(int accountId)
    {
        return getAccount(accountId).getTransactionHistory();
    }

    public List<TransactionLogEntry> getTransactionLogs()
    {
        return new ArrayList<>(transactionLogs);
    }

    private BankAccount getAccount(int accountId)
    {
        for (BankAccount account : accounts) {
            if (account.getAccountId() == accountId) {
                return account;
            }
        }
        throw new IllegalArgumentException("No such account with ID " + accountId);
    }

}