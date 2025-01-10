package org.concurrentprogramming.bankingSystem;

import java.util.Arrays;

public class BankingSystemExample {
    public static void main (String[] args)
    {
        BankAccount account1 = new BankAccount(1, 1000.0);
        BankAccount account2 = new BankAccount(2,  500.0);
        BankAccount account3 = new BankAccount(3,  300.0);

        TransactionSystem bank = new TransactionSystem(Arrays.asList(account1, account2, account3));

        // Sample transactions
        Thread t1 = new Thread(() -> {
            try {
                bank.transfer(1, 2, 600);  // Likely to succeed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                bank.transfer(2, 3, 800);  // Might fail if insufficient after t1
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                bank.transfer(1, 3, 900);  // Could also fail
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final balances
        System.out.println("Final Balances:");
        System.out.println("Account 1: " + bank.readBalance(1)
                + " (History: " + bank.readTransactionHistory(1) + ")");
        System.out.println("Account 2: " + bank.readBalance(2)
                + " (History: " + bank.readTransactionHistory(2) + ")");
        System.out.println("Account 3: " + bank.readBalance(3)
                + " (History: " + bank.readTransactionHistory(3) + ")");

        // Print the transaction logs
        System.out.println("\nTransaction Logs:");
        for (TransactionLogEntry entry : bank.getTransactionLogs()) {
            System.out.println(entry);
        }
    }

}
