package com.raw;

import com.raw.beans.XmlBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author raw
 * @date 2021/2/18
 */
public class BeanLoadApplication {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        XmlBean xmlBean = (XmlBean) context.getBean("xmlBean");
        // XML方式Bean装配
        xmlBean.hello();
        // @Configuration+@Bean注解方式Bean装配
        xmlBean.printAnnotationBean();
        // @Component方式Bean装配
        xmlBean.printComponentBean();
    }
}
