package com.raw.switchdatasource.annotation;

import java.lang.annotation.*;

/**
 * @author raw
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String value() default "master";
}
