package com.flyingpig.chat.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.flyingpig.chat.util.JwtUtil;
import com.flyingpig.chat.util.cache.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

import static com.flyingpig.chat.dataobject.constant.RedisConstants.USER_LOGIN_KEY;

@Component
@Slf4j
public class AuthWsInterceptor extends HttpSessionHandshakeInterceptor {

    private static CacheUtil cacheUtil;

    @Autowired
    public void setStudentMapper(CacheUtil cacheUtil) {
        AuthWsInterceptor.cacheUtil = cacheUtil;
    }



    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String tokenStr = request.getHeaders().getFirst("Authorization");

        // 情况1：缺少 Authorization 头部
        if (StringUtils.isEmpty(tokenStr)) {
            log.warn("握手请求缺少 Authorization 头部");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            // 解析 token 并获取用户ID和UUID
            String userId = JwtUtil.parseJwt(tokenStr).getSubject();
            String uuid = JwtUtil.getUUIDFromJWT(tokenStr);

            // 从 Redis 中获取该用户的 UUID 以进行校验
            String redisUUID = cacheUtil.get(USER_LOGIN_KEY + userId, String.class);
            if (redisUUID != null) {
                redisUUID = redisUUID.replace("\"", ""); // 去除双引号
            }

            // UUID 不匹配 - 禁止访问
            if (!uuid.equals(redisUUID)) {
                log.warn("用户ID {} 的 UUID 校验失败：接收到的 UUID = {}, Redis 中的 UUID = {}", userId, uuid, redisUUID);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            // 将 userId 存入 attributes，供后续使用
            attributes.put("userId", userId);
            log.info("用户ID {} 的握手请求成功", userId);
        } catch (Exception exception) {
            // 发生意外异常
            log.error("处理握手请求时发生异常: {}", exception.getMessage(), exception);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        // 正常握手
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }



    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception ex) {
    }
}

