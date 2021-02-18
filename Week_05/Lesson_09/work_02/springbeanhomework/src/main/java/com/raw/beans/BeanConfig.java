package com.raw.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author raw
 * @date 2021/2/18
 */
@Configuration
public class BeanConfig {

    @Bean
    public AnnotationBean getAnnotationBean() {
        return new AnnotationBean();
    }
}
