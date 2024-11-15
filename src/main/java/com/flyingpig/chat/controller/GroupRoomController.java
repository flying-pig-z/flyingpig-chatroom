package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.service.IGroupRoomService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-09-17
 */
@RestController
@RequestMapping("/group-room")
public class GroupRoomController {

    @Autowired
    private IGroupRoomService groupRoomService;

    @GetMapping("/search")
    @ApiOperation("根据群聊名称模糊查询群聊")
    public Result search(String name) {
        return Result.success(groupRoomService.search(name));
    }

    @GetMapping("/user-group-room")
    @ApiOperation("查询用户自己的群聊会话列表")
    public Result listUserGroupRoom() {
        return Result.success(groupRoomService.listUserGroupRoom());
    }


}
