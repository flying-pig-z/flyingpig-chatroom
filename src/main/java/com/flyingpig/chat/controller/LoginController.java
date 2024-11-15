package com.flyingpig.chat.controller;

import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.StatusCode;
import com.flyingpig.chat.dataobject.dto.request.EmailRegisterReq;
import com.flyingpig.chat.dataobject.dto.request.LoginReq;
import com.flyingpig.chat.service.LoginService;
import com.flyingpig.chat.util.EmailUtil;
import com.flyingpig.chat.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.flyingpig.chat.dataobject.constant.RedisConstants.EMAIL_VERIFYCODE_KEY;

@Api("登录注册操作相关的api")
@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/user/login")
    @ApiOperation("用户登录")
    public Result login(@RequestBody LoginReq loginReq) {
        log.info("用户登录,邮件名：{}",loginReq.getEmail());
        try {
            System.out.println(loginReq);
            return loginService.login(loginReq);
        } catch (RedisConnectionFailureException e) {
            return Result.error(StatusCode.SERVERERROR, "redis崩溃");
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(StatusCode.SERVERERROR, e.getMessage());
        }
    }

    @PostMapping("/user/logout")
    @ApiOperation("用户登出")
    public Result logout() {
        return loginService.logout(UserContext.getUser());
    }

    @GetMapping("/email/verificationCode")
    @ApiOperation("用户获取验证码")
    public Result sendEmailVerificationCode(String email) {
        //检查email是否符合格式
        if (!EmailUtil.judgeEmailFormat(email)) {
            return Result.error(StatusCode.SERVERERROR, "邮箱不符合格式");
        }
        loginService.sendVerificationCode(email);
        return Result.success("验证码已发送");
    }

    @PostMapping("/email/register")
    @ApiOperation("通过验证码完成注册")
    public Result emailRegister(@RequestBody EmailRegisterReq emailRegisterReq) {
        System.out.println(emailRegisterReq.getEmail());
        String verificationCode = stringRedisTemplate.opsForValue().get(EMAIL_VERIFYCODE_KEY + emailRegisterReq.getEmail());
        System.out.println(verificationCode);
        if (verificationCode != null && verificationCode.equals(emailRegisterReq.getVerificationCode())) {
            try {
                loginService.registerUser(emailRegisterReq);
            } catch (DuplicateKeyException dke) {
                log.error("用户名或邮箱重复注册");
                Result.error(StatusCode.SERVERERROR, "用户名或邮箱重复注册");
            }
            return Result.success("添加成功");
        } else {
            return Result.error(StatusCode.SERVERERROR, "验证码验证错误");
        }
    }

}