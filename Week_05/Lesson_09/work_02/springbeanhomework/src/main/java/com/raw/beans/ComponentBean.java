package com.raw.beans;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author raw
 * @date 2021/2/18
 */
@Data
@Component
public class ComponentBean {

    private int id;

    private String name;

    public void hello() {
        System.out.println("hello " + this.name + ", ComponentBean");
    }
}
