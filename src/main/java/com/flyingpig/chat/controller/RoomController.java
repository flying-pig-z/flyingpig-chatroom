package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.dto.response.RoomSession;
import com.flyingpig.chat.service.IGroupRoomService;
import com.flyingpig.chat.service.IPrivateRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
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
    private IGroupRoomService groupRoomService;

    @Autowired
    private IPrivateRoomService privateRoomService;

    @GetMapping("")
    @ApiOperation("查询用户所有聊天会话列表以及历史聊天记录")
    public Result listRoomByUserId() {
        List<RoomSession> roomSessionList = privateRoomService.listUserPrivateRoomSession();
        roomSessionList.addAll(groupRoomService.listUserGroupRoomSession());
        return Result.success(roomSessionList);
    }
}
