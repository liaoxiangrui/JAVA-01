package com.raw.switchdatasource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("com.raw.switchdatasource.mapper")
public class SwitchdatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwitchdatasourceApplication.class, args);
    }
}
