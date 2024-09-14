package com.flyingpig.chat.config;

import com.flyingpig.chat.interceptor.AuthWsInterceptor;
import com.flyingpig.chat.websocket.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatHandler(), "/websocket/chat")
                .setAllowedOrigins("*")  // 允许所有来源
                .addInterceptors(new AuthWsInterceptor());  // 可选：支持会话
    }
}
