package com.raw;

import java.util.concurrent.FutureTask;

public class Case3_FutureTask {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        FutureTask<Integer> futureTask = new FutureTask<>(Demo::sum);
        new Thread(futureTask).start();
        try {
            System.out.println("异步计算结果为：" + futureTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
