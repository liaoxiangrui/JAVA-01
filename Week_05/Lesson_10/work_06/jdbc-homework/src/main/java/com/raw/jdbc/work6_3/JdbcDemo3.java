package com.raw.jdbc.work6_3;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;

/**
 * 配置hikari连接池
 *
 * @author raw
 * @date 2021/2/20
 */
public class JdbcDemo3 {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/jdbc_demo?serverTimezone=Asia/Shanghai";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123";
    private static final HikariConfig CONFIG = new HikariConfig();

    static {
        CONFIG.setJdbcUrl(JDBC_URL);
        CONFIG.setUsername(JDBC_USER);
        CONFIG.setPassword(JDBC_PASSWORD);
        CONFIG.setConnectionTimeout(2000);
        CONFIG.setIdleTimeout(60000);
        CONFIG.setMaximumPoolSize(10);
    }

    private static final DataSource DS = new HikariDataSource(CONFIG);

    public static void main(String[] args) throws SQLException {
        dsAdd("hikari", 18);
    }

    public static void dsAdd(String name, int age) throws SQLException {
        String sql = "INSERT INTO `user`(name, age) VALUES (?,?)";
        try (Connection conn = DS.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setInt(2, age);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    while (rs.next()) {
                        int userId = rs.getInt(1);
                        System.out.println("已添加的用户ID为" + userId);
                    }
                }
            }
        }
    }

}
