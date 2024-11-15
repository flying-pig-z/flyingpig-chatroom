package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("用户操作相关的api")
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    UserService userService;


    @ApiOperation("搜索用户")
    @GetMapping("/search")
    public Result listRoomByUserId(String searchKey) {
        return Result.success(userService.searchUser(searchKey));
    }

    @ApiOperation("获取自身的信息")
    @GetMapping("/info")
    public Result getUserInfoByUserId() {
        return Result.success(userService.getUserInfoByUserId());
    }

    @ApiOperation("获取用户的信息")
    @GetMapping("/info-list")
    public Result listUserInfosByUserIdList(@RequestParam List<Long> userIds) {
        return Result.success(userService.listUserInfosByUserIdList(userIds));
    }
}
