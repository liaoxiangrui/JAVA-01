package com.raw.customize.spring.boot.autoconfigure;

import lombok.Data;

import java.util.List;

/**
 * @author raw
 * @date 2021/2/20
 */
@Data
public class Klass {

    List<Student> students;

    public void dong(){
        System.out.println(this.getStudents());
    }
}
