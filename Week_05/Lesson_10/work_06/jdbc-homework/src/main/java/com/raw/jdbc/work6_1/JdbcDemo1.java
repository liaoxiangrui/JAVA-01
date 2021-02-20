package com.raw.jdbc.work6_1;

import java.sql.*;

/**
 * 原生JDBC增删查改
 *
 * @author raw
 * @date 2021/2/20
 */
public class JdbcDemo1 {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/jdbc_demo?serverTimezone=Asia/Shanghai";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123";

    public static void main(String[] args) throws SQLException {
        int addRs = add("rawliao", 27);
        System.out.println("已添加" + addRs + "个用户！");

        query("raw");

        int updateRs = update(18);
        System.out.println("已修改" + updateRs + "个用户!");

        int delRs = delete("rawliao");
        System.out.println("已删除" + delRs + "个用户！");
    }

    public static int add(String name, int age) throws SQLException {
        String sql = "INSERT INTO `user`(name, age) VALUES ('" + name + "'," + age + ")";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            try (Statement statement = conn.createStatement()) {
                return statement.executeUpdate(sql);
            }
        }
    }

    public static void query(String name) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE name LIKE '%" + name + "%'";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            try (Statement statement = conn.createStatement()) {
                try (ResultSet rs = statement.executeQuery(sql)) {
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String username = rs.getString(2);
                        int age = rs.getInt(3);
                        System.out.println("userId " + id + ", username " + username + ", age" + age);
                    }
                }
            }
        }
    }

    public static int update(int age) throws SQLException {
        String sql = "UPDATE `user` SET age = " + age + " WHERE name = 'rawliao'";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            try (Statement statement = conn.createStatement()) {
                return statement.executeUpdate(sql);
            }
        }
    }

    public static int delete(String name) throws SQLException {
        String sql = "DELETE FROM `user` WHERE name = '" + name + "'";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            try (Statement statement = conn.createStatement()) {
                return statement.executeUpdate(sql);
            }
        }
    }
}
