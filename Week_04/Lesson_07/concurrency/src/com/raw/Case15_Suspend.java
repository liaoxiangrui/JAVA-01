package com.raw;

import java.util.concurrent.atomic.AtomicInteger;

public class Case15_Suspend {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Thread main = Thread.currentThread();
        Thread thread = new Thread(() -> {
            result.set(Demo.sum());
            main.resume();
        });
        thread.start();
        main.suspend();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
