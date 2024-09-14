package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.StatusCode;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.UserMapper;
import com.flyingpig.chat.service.LoginService;
import com.flyingpig.chat.util.JwtUtil;
import com.flyingpig.chat.util.cache.CacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static com.flyingpig.chat.dataobject.constant.RedisConstants.USER_LOGIN_KEY;
import static com.flyingpig.chat.dataobject.constant.RedisConstants.USER_LOGIN_TTL;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;

    private final CacheUtil cacheUtil;

    @Override
    public Result login(User user) {
        // 查询用户信息
        User selectUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, user.getEmail()));
        // 用户不存在抛出异常
        if (Objects.isNull(selectUser)) {
            return Result.error(StatusCode.SERVERERROR, "用户名不存在");
        }
        // 密码错误抛出异常
        if (!new BCryptPasswordEncoder().matches(user.getPassword(), selectUser.getPassword())) {
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
}
