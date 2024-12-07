package com.flyingpig.chat.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.chat.dataobject.dto.response.UserInfo;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.UserMapper;
import com.flyingpig.chat.service.UserService;
import com.flyingpig.chat.util.AliOSSUtils;
import com.flyingpig.chat.util.UserIdContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    AliOSSUtils aliOSSUtils;


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
        User user = userMapper.selectById(UserIdContext.getUserId());
        return new UserInfo(user.getId(), user.getUsername(), user.getAvatar());
    }

    @Override
    public List<UserInfo> listUserInfosByUserIdList(List<Long> userIds) {
        List<User> userList = userMapper.selectBatchIds(userIds);
        List<User> resultList = new ArrayList<>();
        for (Long userId : userIds) {
            for (User user : userList) {
                if (user.getId().equals(userId)) {
                    resultList.add(user);
                    break;
                }
            }
        }
        List<UserInfo> userInfoList = new ArrayList<>();
        for (User user : resultList) {
            userInfoList.add(new UserInfo(user.getId(), user.getUsername(), user.getAvatar()));
        }
        return userInfoList;
    }

    @Override
    public void modifyUserInfo(String username, String password, MultipartFile avatarFile) {
        User user = new User();
        user.setId(Long.parseLong(UserIdContext.getUserId()));
        if (password != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(password));
        }
        try {
            // 更新现有的头像
            if (avatarFile != null) {
                aliOSSUtils.deleteFileByUrl(userMapper.selectById(UserIdContext.getUserId()).getAvatar());
                user.setAvatar(aliOSSUtils.upload(avatarFile));
            }
        } catch (IOException ioException) {
            throw new RuntimeException("用户头像上传OSS异常");
        }
        if (username != null) {
            user.setUsername(username);
        }
        userMapper.updateById(user);
    }


}