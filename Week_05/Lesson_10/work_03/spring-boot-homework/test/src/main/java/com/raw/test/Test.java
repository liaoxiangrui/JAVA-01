package com.raw.test;

import com.raw.customize.spring.boot.autoconfigure.Klass;
import com.raw.customize.spring.boot.autoconfigure.School;
import com.raw.customize.spring.boot.autoconfigure.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @author raw
 * @date 2021/2/20
 */
@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    private Student student;
    @Autowired
    private Klass klass;
    @Autowired
    private School school;

    @GetMapping("/stu")
    public void printStudent() {
        student.init();
    }

    @GetMapping("/kla")
    public void printKlass() {
        klass.setStudents(Collections.singletonList(student));
        klass.dong();
    }

    @GetMapping("/sch")
    public void printSchool() {
        school.setClass1(klass);
        school.setStudent100(student);
        school.ding();
    }
}
