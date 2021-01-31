package com.raw;

import java.util.concurrent.Callable;

public class Case1_Callable {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Callable<Integer> callable = Demo::sum;
        try {
            System.out.println("异步计算结果为：" + callable.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
