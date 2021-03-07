package com.raw.switchdatasource.aop;

import com.raw.switchdatasource.annotation.DataSource;
import com.raw.switchdatasource.config.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author raw
 * @date 2021/3/6
 */
@Aspect
@Slf4j
@Component
public class DynamicDataSourceAspect {

    @Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, DataSource ds) {
        String dsId = ds.value();
        if (DynamicDataSourceContextHolder.getDataSourceRouterKey().equals(dsId)) {
            log.info("Use DataSource : {} -> {}", dsId, point.getSignature());
        }
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, DataSource ds) {
        log.info("Revert DataSource : {} -> {}", ds.value(), point.getSignature());
        DynamicDataSourceContextHolder.removeDataSourceRouterKey();
    }
}
