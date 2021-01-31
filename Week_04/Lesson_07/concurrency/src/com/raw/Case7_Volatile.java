package com.raw;

import java.util.concurrent.TimeUnit;

public class Case7_Volatile {

    public static volatile int result;

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        new Thread(() -> result = Demo.sum()).start();
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
