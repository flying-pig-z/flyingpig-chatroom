package com.flyingpig.chat.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.StatusCode;
import com.flyingpig.chat.util.JwtUtil;
import com.flyingpig.chat.util.UserContext;
import com.flyingpig.chat.util.cache.CacheUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final CacheUtil cacheUtil;

    @Autowired
    public AuthInterceptor(CacheUtil cacheUtil) {
        this.cacheUtil = cacheUtil;
    }

    // 在控制器方法执行之前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenStr = request.getHeader("Authorization");
        log.info("用户开始访问");

        // 为空不允许握手
        if (StringUtils.isEmpty(tokenStr)) {
            log.warn("用户未提供授权令牌，访问被拒绝");
            returnErrorResponse(response, new Result(StatusCode.UNAUTHORIZED, "未提供授权令牌", null));
            return false;
        }

        String userId;
        try {
            // 判断是否是有效的token
            Claims claims = JwtUtil.parseJwt(tokenStr);
            userId = claims.getSubject();
            // 判断是否在redis里
//            String uuid = claims.getId();
//            if (!uuid.equals(cacheUtil.get(userId, String.class))) {
//                System.out.println(666);
//                return false;
//            }
        } catch (Exception exception) {
            log.error("token解析失败", exception.getMessage(), exception);
            returnErrorResponse(response, new Result(StatusCode.UNAUTHORIZED, "授权令牌无效或已过期", null));
            return false;
        }

        // 记录用户访问的接口信息
        log.info("用户{}访问接口：{}，请求方法：{}", userId, request.getRequestURI(), request.getMethod());
        UserContext.setUser(userId);
        return true; // 返回 true 继续处理请求，false 则中断请求
    }

    // 在控制器方法调用之后执行，但在视图渲染之前调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        UserContext.removeUser();
    }

    /**
     * 返回错误响应
     */
    private void returnErrorResponse(HttpServletResponse response, Result result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置HTTP状态码为401
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(result));
        writer.flush();
        writer.close();
    }
}
