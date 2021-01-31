package com.raw;

import java.util.concurrent.CompletableFuture;

public class Case4_CompletableFuture {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(Demo::sum);
        try {
            System.out.println("异步计算结果为：" + result.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
