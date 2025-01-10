package org.concurrentprogramming.cofeeShop;

public class Barista implements Runnable{
    private CoffeeShop shop;
    Barista(CoffeeShop shop)
    {
        this.shop = shop;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i ++)
        {
            shop.prepareOrder();
        }
    }
}
