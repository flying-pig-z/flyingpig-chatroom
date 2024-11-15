package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.service.IApplicationService;
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
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    IApplicationService applicationService;

    @PostMapping
    @ApiOperation("发起申请添加好友/群聊")
    public Result publishApplication(Long roomId) {
        return Result.success(applicationService.publishApplication(roomId));
    }

    @GetMapping("/send")
    @ApiOperation("查询发送的申请")
    public Result selectSendApplication() {
        return Result.success(applicationService.selectSendApplication());
    }

    @GetMapping("/receive")
    @ApiOperation("查询发送的申请")
    public Result selectReceiveApplication() {
        return Result.success(applicationService.selectReceiveApplication());
    }

    @PutMapping
    @ApiOperation("修改申请的状态")
    public Result judgeApplication(Long applicationId, Byte status) {
        applicationService.judgeApplication(applicationId, status);
        return Result.success();
    }

}
