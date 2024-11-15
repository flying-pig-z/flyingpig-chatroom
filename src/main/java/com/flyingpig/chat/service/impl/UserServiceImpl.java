package com.flyingpig.chat.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.chat.dataobject.dto.request.EmailRegisterReq;
import com.flyingpig.chat.dataobject.dto.response.UserInfo;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.UserMapper;
import com.flyingpig.chat.service.UserService;
import com.flyingpig.chat.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public List<UserInfo> searchUser(String searchKey) {
        // 执行查询，获取符合条件的用户列表
        List<User> userList = userMapper.selectList(new LambdaQueryWrapper<User>()
                .like(User::getEmail, searchKey).or().like(User::getUsername, searchKey));
        // 封装成用户信息集合返回
        return userList.stream()
                .map(user -> new UserInfo(user.getId(), user.getUsername(), user.getAvatar()))
                .collect(Collectors.toList());
    }

    @Override
    public UserInfo getUserInfoByUserId() {
        User user = userMapper.selectById(UserContext.getUser());
        return new UserInfo(user.getId(), user.getUsername(), user.getAvatar());
    }

    @Override
    public List<UserInfo> listUserInfosByUserIdList(List<Long> userIds) {
        List<User> userList = userMapper.selectBatchIds(userIds);
        List<UserInfo> userInfoList = new ArrayList<>();
        for (User user : userList) {
            userInfoList.add(new UserInfo(user.getId(), user.getUsername(), user.getAvatar()));
        }
        return userInfoList;
    }




}