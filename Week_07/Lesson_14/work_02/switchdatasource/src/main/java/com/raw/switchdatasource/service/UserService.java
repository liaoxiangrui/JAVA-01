package com.raw.switchdatasource.service;

import com.raw.switchdatasource.model.User;

import java.util.List;

public interface UserService {

    /**
     * 查询用户表
     *
     * @return
     */
    List<User> selectAll();

    /**
     * 添加用户
     *
     * @return
     */
    int addUser();
}
