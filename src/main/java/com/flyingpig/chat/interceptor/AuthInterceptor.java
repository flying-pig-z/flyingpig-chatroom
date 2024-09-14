package com.flyingpig.chat.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.flyingpig.chat.util.JwtUtil;
import com.flyingpig.chat.util.UserContext;
import com.flyingpig.chat.util.cache.CacheUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    CacheUtil cacheUtil;

    // 在控制器方法执行之前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenStr = request.getHeader("Authorization");
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
        return true; // 返回 true 继续处理请求，false 则中断请求
    }

    // 在控制器方法调用之后执行，但在视图渲染之前调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        UserContext.removeUser();
    }
}
