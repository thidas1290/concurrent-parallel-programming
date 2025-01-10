package org.concurrentprogramming.cofeeShop;

public class Customer implements Runnable{
    private CoffeeShop shop;
    private int iterations;

    Customer(CoffeeShop shop, int iterations)
    {
        this.shop = shop;
        this.iterations = iterations;
    }

    @Override
    public void run()
    {
        for(int i = 0; i < iterations; i ++)
        {
            shop.placeOrder(new Coffee());
        }
    }
}
