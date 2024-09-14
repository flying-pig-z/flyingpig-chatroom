package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.StatusCode;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.service.LoginService;
import com.flyingpig.chat.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("登录操作相关的api")
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result login(@RequestBody User user) {
        try {
            System.out.println(user);
            return loginService.login(user);
        } catch (RedisConnectionFailureException e) {
            return Result.error(StatusCode.SERVERERROR, "redis崩溃");
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(StatusCode.SERVERERROR, e.getMessage());
        }
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result logout() {
        return loginService.logout(UserContext.getUser());
    }

}