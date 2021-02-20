package com.raw.customize.spring.boot.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author raw
 * @date 2021/2/20
 */
@Configuration
public class DemoAutoConfiguration {

    @Bean
    public Student getStudent() {
        return new Student(1,"raw");
    }

    @Bean
    public Klass getKlass() {
        return new Klass();
    }

    @Bean
    public School getSchool() {
        return new School();
    }
}
