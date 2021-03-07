package com.raw.switchdatasource.mapper;

import com.raw.switchdatasource.model.User;

import java.util.List;

/**
 * @author raw
 * @date 2021/3/6
 */
public interface UserMapper {

    List<User> selectAll();

    int save(User user);
}
