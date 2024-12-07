package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.RoomInfo;
import com.flyingpig.chat.dataobject.dto.response.RoomWithReadMessage;
import com.flyingpig.chat.service.IGroupRoomService;
import com.flyingpig.chat.service.IPrivateRoomService;
import com.flyingpig.chat.service.IRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-09-20
 */
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    @Autowired
    IRoomService roomService;

    @Autowired
    private IGroupRoomService groupRoomService;

    @Autowired
    private IPrivateRoomService privateRoomService;

    @GetMapping("/list")
    @ApiOperation("查询用户所有聊天会话列表")
    public Result listUserRoom(@RequestParam Byte type) {
        List<RoomInfo> roomInfos = new ArrayList<>();
        if (type.equals(RoomType.PRIVATE_ROOM)) {
            roomInfos.addAll(privateRoomService.listUserPrivateRoom());
        } else if (type.equals(RoomType.GROUP_ROOM)) {
            roomInfos.addAll(groupRoomService.listUserGroupRoom());
        } else {
            roomInfos.addAll(privateRoomService.listUserPrivateRoom());
            roomInfos.addAll(groupRoomService.listUserGroupRoom());
        }
        return Result.success(roomInfos);
    }


    @GetMapping("/with-read-msg/list")
    @ApiOperation("查询用户所有聊天会话列表及其已读消息")
    public Result listUserRoomWithReadMessage() {
        List<RoomWithReadMessage> roomSessionList = privateRoomService.listUserPrivateRoomWithReadMessage();
        roomSessionList.addAll(groupRoomService.listUserGroupRoomWithReadMessage());
        return Result.success(roomSessionList);
    }

    @GetMapping("/{roomId}/member/list")
    @ApiOperation("获取聊天室所有成员列表")
    public Result listMemberList(@PathVariable Long roomId) {
        return Result.success(roomService.listMemberList(roomId));
    }
}
