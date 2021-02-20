package com.raw.customize.spring.boot.autoconfigure;

import lombok.Data;

/**
 * @author raw
 * @date 2021/2/20
 */
@Data
public class School {

    Klass class1;

    Student student100;

    public void ding() {

        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student100);

    }
}
