package com.raw.switchdatasource.test;

import com.alibaba.fastjson.JSON;
import com.raw.switchdatasource.model.User;
import com.raw.switchdatasource.service.UserService;
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
    private UserService userService;

    @Test
    public void save() {
        assertEquals(1, userService.addUser());
    }

    @Test
    public void selectAll() {
        List<User> users = userService.selectAll();
        System.out.println(JSON.toJSONString(users));
    }
}
