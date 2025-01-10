package org.concurrentprogramming.bankingSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BankAccount {
    private final int accountId;
    private volatile double balance;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    private final List<String> transactionHistory = Collections.synchronizedList(new ArrayList<>());

    public BankAccount(int accountId, double initialBalance)
    {
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public double getBalance()
    {
        rwLock.readLock().lock();
        try {
            return balance;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<String> getTransactionHistory()
    {
        rwLock.readLock().lock();
        try {
            return new ArrayList<>(transactionHistory);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private void deposit(double amount)
    {
        this.balance += amount;
        transactionHistory.add("Deposited " + amount + ", new balance: " + this.balance);
    }

    private void withdraw(double amount)
    {
        this.balance -= amount;
        transactionHistory.add("Withdrew " + amount + ", new balance: " + this.balance);
    }

    // Locking methods for external usage
    void writeLock() {
        rwLock.writeLock().lock();
    }

    void writeUnlock() {
        rwLock.writeLock().unlock();
    }

    // Methods for direct deposit/withdraw (assumes external locking)
    void depositUnsafe(double amount) {
        deposit(amount);
    }

    void withdrawUnsafe(double amount) {
        withdraw(amount);
    }
}
