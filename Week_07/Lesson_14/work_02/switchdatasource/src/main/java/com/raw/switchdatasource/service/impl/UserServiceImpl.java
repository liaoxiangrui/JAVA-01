package com.raw.switchdatasource.service.impl;

import com.raw.switchdatasource.mapper.UserMapper;
import com.raw.switchdatasource.model.User;
import com.raw.switchdatasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author raw
 * @date 2021/3/6
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public int addUser() {
        return userMapper.save(new User(1, "LXR", 20));
    }
}
