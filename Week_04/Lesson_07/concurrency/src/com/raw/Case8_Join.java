package com.raw;

import java.util.concurrent.atomic.AtomicInteger;

public class Case8_Join {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Thread thread = new Thread(() -> result.set(Demo.sum()));
        thread.start();
        thread.join();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
