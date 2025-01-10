package org.concurrentprogramming.cofeeShop;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CoffeeShop {
    private static final int MAX_SIZE = 10;
    Queue<Coffee> queue = new LinkedList<Coffee>();
    ReentrantLock lock = new ReentrantLock(true);
    Condition notEmpty = lock.newCondition();
    Condition notFull = lock.newCondition();
    CoffeeShop()
    {
    }

    public void placeOrder(Coffee coffee)
    {
        lock.lock();
        try
        {
            while(queue.size() >= MAX_SIZE)
            {
                notFull.await();
            }
            queue.add(coffee);
            System.out.println(Thread.currentThread().getName() + " placing coffee");
            notEmpty.signalAll();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public Coffee prepareOrder()
    {
        lock.lock();
        Coffee coffee;
        try
        {
            while (queue.isEmpty())
            {
                notEmpty.await();
            }
            System.out.println(Thread.currentThread().getName() + " preparing coffee");
            coffee = queue.remove();
            notFull.signalAll();
            return coffee;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {

            lock.unlock();


        }
        return null;
    }
}
