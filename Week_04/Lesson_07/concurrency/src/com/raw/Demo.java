package com.raw;

public class Demo {

    public static int sum() {
        return fibo(36);
    }

    public static int fibo(int a) {
        if (a < 2) {
            return a;
        }
        return fibo(a - 1) + fibo(a - 2);
    }
}
