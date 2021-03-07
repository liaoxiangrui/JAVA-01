package com.raw.switchdatasource.test;

import com.alibaba.fastjson.JSON;
import com.raw.switchdatasource.mapper.UserMapper;
import com.raw.switchdatasource.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author raw
 * @date 2021/3/6
 */
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void save() {
        assertEquals(1, userMapper.save(new User(11, "master", 22)));
    }

    @Test
    public void selectAll() {
        List<User> users = userMapper.selectAll();
        System.out.println(JSON.toJSONString(users));
    }
}
