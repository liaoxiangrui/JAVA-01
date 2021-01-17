package com.raw;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        InputStream is = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int num;
        byte[] bytes = new byte[0];
        try {
            is = new FileInputStream("C:/Users/a4609/Desktop/Hello.xlass");
            while ((num = is.read()) != -1) {
                os.write(255 - num);
            }
            bytes = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}
