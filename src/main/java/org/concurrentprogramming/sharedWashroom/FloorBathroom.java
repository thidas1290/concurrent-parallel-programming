package org.concurrentprogramming.sharedWashroom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class FloorBathroom {
    private static final Semaphore bathroomStalls = new Semaphore(6, true);
    private static final int TOTAL_PEOPLE = 100;

    public static void main(String[] args) {

        List<Thread> threads = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= TOTAL_PEOPLE; i++) {
            // Randomly assign user type for demonstration
            String userType = random.nextBoolean() ? "Employee" : "Student";

            // Create a thread for each user
            int finalI = i;
            Thread t = new Thread(() -> useBathroom(userType, finalI));
            threads.add(t);
            t.start();
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted: " + e.getMessage());
            }
        }

        System.out.println("All users have finished using the bathroom.");
    }

    private static void useBathroom(String userType, int personId)
    {
        try {
            // Acquire a permit (stall)
            bathroomStalls.acquire();

            System.out.println(userType + " " + personId + " has entered a stall.");

            // Simulate the time spent in the bathroom
            Thread.sleep((long) (1500 ));

            System.out.println(userType + " " + personId + " is leaving the stall.");

        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted: " + e.getMessage());
        } finally {
            // releasing permit
            bathroomStalls.release();
        }
    }
}

