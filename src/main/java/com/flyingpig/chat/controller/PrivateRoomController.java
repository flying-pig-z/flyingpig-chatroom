package com.flyingpig.chat.controller;

import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.service.IPrivateRoomService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private-room")
public class PrivateRoomController {

    @Autowired
    IPrivateRoomService privateRoomService;

    @GetMapping("/user-private-room")
    @ApiOperation("查询用户自己的私聊会话列表")
    public Result listUserPrivateRoom() {
        return Result.success(privateRoomService.listUserPrivateRoom());
    }
}
