package com.raw;

import java.io.*;
import java.lang.reflect.Method;

/**
 * @author raw
 * @date 2021/1/8
 */
public class ClassLoaderHomework extends ClassLoader {
    public static void main(String[] args) {
        try {
            Class<?> cl = new ClassLoaderHomework().findClass("Hello");
            Method method = cl.getMethod("hello");
            method.invoke(cl.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int num;
        try {
            InputStream is = new FileInputStream("C:/Users/a4609/Desktop/Hello.xlass");
            while ((num = is.read()) != -1) {
                stream.write(255 - num);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = stream.toByteArray();
        return defineClass(name, bytes, 0, bytes.length);
    }
}
