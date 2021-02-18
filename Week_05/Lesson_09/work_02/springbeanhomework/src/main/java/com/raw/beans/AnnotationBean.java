package com.raw.beans;

import lombok.Data;

/**
 * @author raw
 * @date 2021/2/18
 */
@Data
public class AnnotationBean {

    private int id;

    private String name;

    public void hello() {
        System.out.println("hello " + this.name + ", AnnotationBean");
    }
}
