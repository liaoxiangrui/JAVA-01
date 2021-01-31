package com.raw;

import java.util.concurrent.CompletableFuture;

public class Case5_CompletableFuture {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int result = CompletableFuture.supplyAsync(Demo::sum).join();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
