package com.flyingpig.chat.util;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public void addSession(String userId, WebSocketSession session) {
        sessionMap.put(userId, session);
    }

    public WebSocketSession getSession(String userId) {
        return sessionMap.get(userId);
    }

    public void removeSession(String userId) {
        sessionMap.remove(userId);
    }

    public void sendMessageToUser(String userId, String message) throws Exception {
        WebSocketSession session = getSession(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}

