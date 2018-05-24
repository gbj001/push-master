package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.entity.User;
import com.xinliangjishipin.pushwms.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User userLogin(String userName, String password){
        return userMapper.getUser(userName, password);
    }
}
