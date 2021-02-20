package com.raw.customize.spring.boot.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author raw
 * @date 2021/2/20
 */
@Data
@AllArgsConstructor
public class Student implements Serializable {

    private int id;
    private String name;

    public void init() {
        System.out.println("hello...........");
    }
}
