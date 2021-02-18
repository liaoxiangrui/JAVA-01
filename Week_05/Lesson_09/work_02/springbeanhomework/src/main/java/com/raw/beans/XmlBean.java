package com.raw.beans;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author raw
 * @date 2021/2/18
 */
@Data
public class XmlBean {

    @Autowired
    private ComponentBean componentBean;
    @Autowired
    private AnnotationBean annotationBean;

    private int id;

    private String name;

    public void hello() {
        System.out.println("hello " + this.name + ", XmlBean");
    }

    public void printAnnotationBean() {
        annotationBean.setName("raw");
        annotationBean.hello();
    }

    public void printComponentBean() {
        componentBean.setName("raw");
        componentBean.hello();
    }
}
