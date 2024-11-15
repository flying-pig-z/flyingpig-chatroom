package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.service.IRoomMessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 聊天室聊天消息 前端控制器
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-07
 */
@RestController
@RequestMapping("/room-message")
public class RoomMessageController {

    @Autowired
    IRoomMessageService roomMessageService;

    @GetMapping("/list")
    @ApiOperation("获取会话信息")
    public Result listRoomMessage(Long roomId) {
        return Result.success(roomMessageService.listRoomMessage(roomId));
    }

}
