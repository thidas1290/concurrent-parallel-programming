package org.concurrentprogramming.cofeeShop;

public class CoffeeShopExample {
    public static void main(String[] args) throws InterruptedException {
        CoffeeShop shop = new CoffeeShop();

        Thread c1 = new Thread(new Customer(shop, 10), "Customer-1");
        Thread c2 = new Thread(new Customer(shop, 10), "Customer-2");
        Thread b1 = new Thread(new Barista(shop), "Barista-1");

        c1.start();
        c2.start();
        b1.start();

        c1.join();
        c2.join();
        b1.join();
    }
}
