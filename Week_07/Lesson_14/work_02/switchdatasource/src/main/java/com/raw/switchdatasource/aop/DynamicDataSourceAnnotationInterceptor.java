package com.raw.switchdatasource.aop;

import com.raw.switchdatasource.annotation.DataSource;
import com.raw.switchdatasource.config.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author raw
 * @date 2021/3/6
 */
@Slf4j
public class DynamicDataSourceAnnotationInterceptor implements MethodInterceptor {

    /**
     * 缓存方法注解值
     */
    private static final Map<Method, String> METHOD_CACHE = new HashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String datasource = determineDatasource(invocation);
        DynamicDataSourceContextHolder.setDataSourceRouterKey(datasource);
        return invocation.proceed();
    }

    private String determineDatasource(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (METHOD_CACHE.containsKey(method)) {
            return METHOD_CACHE.get(method);
        } else {
            DataSource ds = method.isAnnotationPresent(DataSource.class) ? method.getAnnotation(DataSource.class)
                    : AnnotationUtils.findAnnotation(method.getDeclaringClass(), DataSource.class);
            METHOD_CACHE.put(method, ds.value());
            return ds.value();
        }
    }
}
