package org.concurrentprogramming.bankingSystem;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

class TransactionLogEntry {
    private static final Random RANDOM = new Random();

    private final long transactionId;          // Randomly generated ID
    private final LocalDateTime timestamp;
    private final int fromAccountId;
    private final int toAccountId;
    private final double amount;

    // Recording partial commit progress
    private boolean withdrawnFromSource;
    private boolean depositedToTarget;

    // Indicate if the transaction eventually succeeded
    private final AtomicBoolean completedSuccessfully = new AtomicBoolean(false);

    public TransactionLogEntry(int fromAccountId, int toAccountId, double amount) {
        // Generate a random long as our transaction ID
        this.transactionId = RANDOM.nextLong();
        this.timestamp = LocalDateTime.now();
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.withdrawnFromSource = false;
        this.depositedToTarget = false;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isWithdrawnFromSource() {
        return withdrawnFromSource;
    }

    public void setWithdrawnFromSource(boolean withdrawnFromSource) {
        this.withdrawnFromSource = withdrawnFromSource;
    }

    public boolean isDepositedToTarget() {
        return depositedToTarget;
    }

    public void setDepositedToTarget(boolean depositedToTarget) {
        this.depositedToTarget = depositedToTarget;
    }

    public boolean isCompletedSuccessfully() {
        return completedSuccessfully.get();
    }

    public void markCompletedSuccessfully() {
        completedSuccessfully.set(true);
    }

    @Override
    public String toString() {
        return String.format("TransactionLogEntry{id=%d, from=%d, to=%d, amount=%.2f, withdrawn=%b, deposited=%b, completed=%b}",
                transactionId, fromAccountId, toAccountId, amount, withdrawnFromSource, depositedToTarget, completedSuccessfully.get());
    }
}