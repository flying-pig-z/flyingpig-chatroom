package com.flyingpig.chat.config;

import com.flyingpig.chat.interceptor.AuthInterceptor;
import com.flyingpig.chat.util.cache.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    CacheUtil cacheUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(cacheUtil))
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/email/register")
                .excludePathPatterns("/email/verificationCode")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger-resources")
                .excludePathPatterns("/v2/api-docs");
    }
}
