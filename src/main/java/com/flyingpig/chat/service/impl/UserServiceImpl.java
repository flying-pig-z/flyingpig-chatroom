package com.flyingpig.chat.service.impl;


import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.UserMapper;
import com.flyingpig.chat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;



    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

}