package com.raw;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Case10_LockCondition {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        AtomicInteger result = new AtomicInteger();
        Thread thread = new Thread(() -> {
            lock.lock();
            result.set(Demo.sum());
            condition.signalAll();
            lock.unlock();
        });
        thread.start();
        lock.lock();
        try {
            condition.await();
            System.out.println("异步计算结果为：" + result);
            System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        } finally {
            lock.unlock();
        }
    }
}
