package com.raw.switchdatasource.config;

import lombok.extern.slf4j.Slf4j;

/**
 * @author raw
 * @date 2021/3/6
 */
@Slf4j
public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    public static String getDataSourceRouterKey() {
        return HOLDER.get();
    }

    public static void setDataSourceRouterKey(String dataSourceRouterKey) {
        log.info("切换至{}数据源", dataSourceRouterKey);
        HOLDER.set(dataSourceRouterKey);
    }

    public static void removeDataSourceRouterKey() {
        HOLDER.remove();
    }
}
