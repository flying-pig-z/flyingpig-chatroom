package com.flyingpig.chat.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.flyingpig.chat.util.JwtUtil;
import com.flyingpig.chat.util.UserContext;
import com.flyingpig.chat.util.cache.CacheUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

public class AuthWsInterceptor extends HttpSessionHandshakeInterceptor {

    @Autowired
    CacheUtil cacheUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String tokenStr = request.getHeaders().getFirst("Authorization");
        // 为空不允许握手
        if (StringUtils.isEmpty(tokenStr)) {
            return false;
        }
        String userId;
        try {
            // 判断是否是有效的token
            Claims claims = JwtUtil.parseJwt(tokenStr);
            userId = claims.getSubject();
            // 判断是否在redis里
            String uuid = claims.getId();
            if (!uuid.equals(cacheUtil.get(userId, String.class))) {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        UserContext.setUser(userId);
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception ex) {
        UserContext.removeUser();
    }
}

