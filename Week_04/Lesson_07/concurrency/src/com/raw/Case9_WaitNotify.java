package com.raw;

import java.util.concurrent.atomic.AtomicInteger;

public class Case9_WaitNotify {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Object obj = new Object();
        Thread thread = new Thread(() -> {
            synchronized (obj) {
                result.set(Demo.sum());
                obj.notifyAll();
            }
        });
        thread.start();
        synchronized (obj) {
            obj.wait();
            System.out.println("异步计算结果为：" + result);
            System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        }
    }
}
