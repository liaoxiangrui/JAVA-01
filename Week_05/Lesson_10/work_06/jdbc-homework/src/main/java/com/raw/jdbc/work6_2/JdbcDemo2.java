package com.raw.jdbc.work6_2;

import com.raw.jdbc.work6_1.JdbcDemo1;

import java.sql.*;
import java.util.Arrays;

/**
 * PreparedStatement预编译、JDBC事务、批量插入
 *
 * @author raw
 * @date 2021/2/20
 */
public class JdbcDemo2 {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/jdbc_demo?serverTimezone=Asia/Shanghai";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123";

    public static void main(String[] args) throws SQLException {
        psAdd("raw", 100);
        jdbcTransaction();
        batchAdd(10);
    }

    public static void psAdd(String name, int age) throws SQLException {
        String sql = "INSERT INTO `user`(name, age) VALUES (?,?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
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

    public static void jdbcTransaction() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        try {
            conn.setAutoCommit(false);
            JdbcDemo1.add("rawliao", 20);
            JdbcDemo1.update(25);
            conn.commit();
            System.out.println("事务提交成功！");
        } catch (SQLException e) {
            System.out.println("事务提交失败！");
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public static void batchAdd(int num) throws SQLException {
        String sql = "INSERT INTO `user`(name, age) VALUES (?,?)";
//        String sql1 = "UPDATE `user` SET age = ? WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int i = 0;
                while (i < num) {
                    ps.setString(1, "liao" + i);
                    ps.setInt(2, i);
                    ps.addBatch();
                    i++;
                }
                int[] rs = ps.executeBatch();
                Arrays.stream(rs).forEach(System.out::println);
            }
        }
    }
}
