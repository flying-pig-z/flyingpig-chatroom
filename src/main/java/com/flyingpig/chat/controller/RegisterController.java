package com.flyingpig.chat.controller;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.StatusCode;
import com.flyingpig.chat.dataobject.dto.request.EmailRegisterReq;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.service.UserService;
import com.flyingpig.chat.util.EmailUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.chat.dataobject.constant.RedisConstants.EMAIL_VERIFYCODE_KEY;
import static com.flyingpig.chat.dataobject.constant.RedisConstants.EMAIL_VERIFYCODE_TTL;


@RestController
@RequestMapping("/email")
@Api("与邮件处理相关的api")
public class RegisterController {
    @Autowired
    UserService userService;
    @Resource
    private JavaMailSenderImpl mailSender;
    //这里要使用工具类，不然各个方法之间的redis数据无法共用

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${spring.mail.username}")
    private String emailUserName;

    @GetMapping("/verificationCode")
    @ApiOperation("用户获取验证码")
    public Result sendEmailVerificationCode(String email) {
        //检查email是否符合格式
        if (!EmailUtil.judgeEmailFormat(email)) {
            return Result.error(StatusCode.SERVERERROR, "邮箱不符合格式");
        }
        String verificationCode = EmailUtil.createVerificationCode();
        //发送
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailUserName);//设置发件qq邮箱
        message.setTo(email);      // 设置收件人邮箱地址
        message.setSubject("验证码");    // 设置邮件主题
        message.setText(verificationCode);   // 设置邮件正文
        mailSender.send(message);
        //存入缓存
        stringRedisTemplate.opsForValue().set(EMAIL_VERIFYCODE_KEY + email, verificationCode, EMAIL_VERIFYCODE_TTL, TimeUnit.SECONDS);
        return Result.success("验证码已发送");
    }

    @PostMapping("/register")
    @ApiOperation("通过验证码完成注册")
    public Result emailRegister(@RequestBody EmailRegisterReq emailRegisterReq) {
        System.out.println(emailRegisterReq.getEmail());
        String verificationCode = stringRedisTemplate.opsForValue().get(EMAIL_VERIFYCODE_KEY + emailRegisterReq.getEmail());
        System.out.println(verificationCode);
        if (verificationCode != null && verificationCode.equals(emailRegisterReq.getVerificationCode())) {
            //添加用户
            User user = new User();
            user.setUsername(emailRegisterReq.getUsername());
            user.setPassword(new BCryptPasswordEncoder().encode(emailRegisterReq.getPassword()));
            user.setEmail(emailRegisterReq.getEmail());
            userService.addUser(user);
            return Result.success("添加成功,请联系管理员审核");
        } else {
            return Result.error(StatusCode.SERVERERROR, "验证码验证错误");
        }

    }
}
