package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.StatusCode;
import com.flyingpig.chat.dataobject.dto.request.EmailRegisterReq;
import com.flyingpig.chat.dataobject.dto.request.LoginReq;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.UserMapper;
import com.flyingpig.chat.service.LoginService;
import com.flyingpig.chat.util.EmailUtil;
import com.flyingpig.chat.util.JwtUtil;
import com.flyingpig.chat.util.cache.CacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.chat.dataobject.constant.RedisConstants.*;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;

    private final CacheUtil cacheUtil;

    @Resource
    private JavaMailSenderImpl mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 默认头像
    private final String avatarUrl = "https://flying-pig-z.oss-cn-beijing.aliyuncs.com/%E9%A3%9E%E7%BF%94%E7%9A%84%E7%8C%AA.jpeg";

    @Value("${spring.mail.username}")
    private String emailUserName;

    @Override
    public Result login(LoginReq loginReq) {
        // 查询用户信息
        User selectUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, loginReq.getEmail()));
        // 用户不存在抛出异常
        if (Objects.isNull(selectUser)) {
            return Result.error(StatusCode.SERVERERROR, "用户名不存在");
        }
        // 密码错误抛出异常
        if (!new BCryptPasswordEncoder().matches(loginReq.getPassword(), selectUser.getPassword())) {
            return Result.error(StatusCode.SERVERERROR, "用户名或者密码错误");
        }
        // 生成JWT
        String uuid = JwtUtil.getUUID();
        String jwt = JwtUtil.createJWT(selectUser.getId().toString(), JwtUtil.JWT_TTL, uuid);
        // 登录用户存入缓存，键为USER_LOGIN_KEY+用户id，值为uuid
        cacheUtil.set(USER_LOGIN_KEY + selectUser.getId(), uuid, USER_LOGIN_TTL, TimeUnit.DAYS);
        //返回
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", jwt);
        hashMap.put("email", selectUser.getEmail());
        hashMap.put("id", selectUser.getId());
        return Result.success(hashMap);
    }


    @Override
    public Result logout(String userId) {
        cacheUtil.delete(USER_LOGIN_KEY + userId);
        return new Result(200, "退出成功", null);
    }

    @Override
    public void registerUser(EmailRegisterReq emailRegisterReq) {
        userMapper.insert(new User(null, emailRegisterReq.getUsername(),
                new BCryptPasswordEncoder().encode(emailRegisterReq.getPassword()),
                emailRegisterReq.getEmail(), avatarUrl));
    }

    @Override
    @Async
    public void sendVerificationCode(String email) {
        // 验证码 邮件主题 邮件正文
        String verificationCode = EmailUtil.createVerificationCode();
        String subject = "【飞猪聊天室】验证码";
        String text = String.format("【飞猪聊天室】验证码：%s，您正在申请注册飞猪聊天室账号" +
                "（若非本人操作，请删除本邮件）", verificationCode);

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailUserName);  // 设置发件邮箱
        message.setTo(email);            // 设置收件邮箱
        message.setSubject(subject);     // 设置邮件主题
        message.setText(text);           // 设置邮件正文
        mailSender.send(message);

        // 存入缓存
        stringRedisTemplate.opsForValue().set(EMAIL_VERIFYCODE_KEY + email, verificationCode, EMAIL_VERIFYCODE_TTL, TimeUnit.SECONDS);
    }
}
