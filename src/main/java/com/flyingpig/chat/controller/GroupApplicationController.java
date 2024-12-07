package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.service.IGroupApplicationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 申请表 前端控制器
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-07
 */
@RestController
@RequestMapping("/group-application")
public class GroupApplicationController {

    @Autowired
    IGroupApplicationService applicationService;

    @PostMapping
    @ApiOperation("发起申请添加群聊")
    public Result publishGroupApplication(Long roomId, String applyMsg) {
        return Result.success(applicationService.publishGroupApplication(roomId, applyMsg));
    }

    @GetMapping("/send")
    @ApiOperation("查询发送的申请")
    public Result selectSendApplication() {
        return Result.success(applicationService.selectSendApplication());
    }

    @GetMapping("/receive")
    @ApiOperation("查询发送的群聊申请")
    public Result selectReceiveApplication() {
        return Result.success(applicationService.selectReceiveApplication());
    }

    @PutMapping("/{applicationId}")
    @ApiOperation("决定同意/不同意群聊申请")
    public Result judgeApplication(@PathVariable Long applicationId, Byte status) {
        return Result.success(applicationService.judgeApplication(applicationId, status));
    }

}
