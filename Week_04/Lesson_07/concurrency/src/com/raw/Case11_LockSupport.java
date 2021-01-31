package com.raw;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class Case11_LockSupport {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Thread main = Thread.currentThread();
        Thread thread = new Thread(() -> {
            result.set(Demo.sum());
            LockSupport.unpark(main);
        });
        thread.start();
        LockSupport.park();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
