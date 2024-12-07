package com.flyingpig.chat.service;

import com.flyingpig.chat.dataobject.dto.response.UserInfo;
import com.flyingpig.chat.dataobject.eneity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    void addUser(User user);

    List<UserInfo> searchUser(String searchKey);

    UserInfo getUserInfoByUserId();

    List<UserInfo> listUserInfosByUserIdList(List<Long> userIds);

    void modifyUserInfo(String username, String password, MultipartFile avatarFile);
}
