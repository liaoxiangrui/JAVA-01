package com.raw.switchdatasource;

import com.raw.switchdatasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.raw.switchdatasource.aop.DynamicDataSourceAnnotationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.raw.switchdatasource.mapper")
public class SwitchdatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwitchdatasourceApplication.class, args);
    }

    @Bean
    public DynamicDataSourceAnnotationAdvisor dynamicDatasourceAnnotationAdvisor() {
        return new DynamicDataSourceAnnotationAdvisor(new DynamicDataSourceAnnotationInterceptor());
    }
}
