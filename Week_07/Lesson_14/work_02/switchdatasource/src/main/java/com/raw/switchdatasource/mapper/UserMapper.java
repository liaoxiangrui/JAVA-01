package com.raw.switchdatasource.mapper;

import com.raw.switchdatasource.annotation.DataSource;
import com.raw.switchdatasource.model.User;

import java.util.List;

/**
 * @author raw
 * @date 2021/3/6
 */
@DataSource("slave")
public interface UserMapper {

    List<User> selectAll();

    @DataSource
    int save(User user);
}
