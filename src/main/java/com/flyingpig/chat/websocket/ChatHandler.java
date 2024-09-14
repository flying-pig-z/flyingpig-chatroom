package com.flyingpig.chat.websocket;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.minlog.Log;
import com.flyingpig.chat.dataobject.dto.request.ChatMessage;
import com.flyingpig.chat.util.UserContext;
import com.flyingpig.chat.util.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理接收到的消息
        System.out.println("Received message: " + message.getPayload());

        // 解析消息内容，假设消息格式为 "userId:message"
        ChatMessage chatMessage = JSON.parseObject(message.getPayload(), ChatMessage.class);
        sessionManager.sendMessageToUser(UserContext.getUser(), chatMessage.getContent());

        // 可以将消息转发到其他用户
        session.sendMessage(new TextMessage("Message received!"));
    }

    // 连接建立时的逻辑
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 保存连接的会话信息
        sessionManager.addSession(UserContext.getUser(), session);
        Log.info("ID为" + UserContext.getUser() + "的用户建立连接");
        // 查询未读消息进行传递

    }

    // 处理传输错误
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接关闭时的逻辑
        sessionManager.removeSession(UserContext.getUser());
        Log.info("ID为" + UserContext.getUser() + "的用户断开连接");
    }
}
