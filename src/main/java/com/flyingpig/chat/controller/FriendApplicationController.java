package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.service.IFriendApplicationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 好友/私聊申请表 前端控制器
 * </p>
 *
 * @author flyingpig
 * @since 2024-12-01
 */
@RestController
@RequestMapping("/friend-application")
public class FriendApplicationController {

    @Autowired
    IFriendApplicationService friendApplicationService;

    @PostMapping
    @ApiOperation("发起申请添加群聊")
    public Result publishFriendApplication(Long userId, String applyMsg) {
        return Result.success(friendApplicationService.publishFriendApplication(userId, applyMsg));
    }

    @GetMapping("/send")
    @ApiOperation("查询发送的申请")
    public Result selectSendApplication() {
        return Result.success(friendApplicationService.selectSendApplication());
    }

    @GetMapping("/receive")
    @ApiOperation("查询发送的群聊申请")
    public Result selectReceiveApplication() {
        return Result.success(friendApplicationService.selectReceiveApplication());
    }

    @PutMapping("/{applicationId}")
    @ApiOperation("决定同意/不同意群聊申请")
    public Result judgeApplication(@PathVariable Long applicationId, Byte status) {
        return Result.success(friendApplicationService.judgeApplication(applicationId, status));
    }

}
