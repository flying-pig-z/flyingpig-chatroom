package com.flyingpig.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyingpig.chat.mapper.GroupRoomMembersMapper;
import com.flyingpig.chat.mapper.PrivateRoomMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> userIdToSessionMap = new ConcurrentHashMap<>();

    private final Map<String, String> sessionIdToUserIdMap = new ConcurrentHashMap<>();

    @Autowired
    GroupRoomMembersMapper groupRoomMembersMapper;

    @Autowired
    PrivateRoomMapper privateRoomMapper;

    @Autowired
    ObjectMapper objectMapper;


    public void addSession(String userId, WebSocketSession session) {
        userIdToSessionMap.put(userId, session); // 便于通过userId获取对应的session
        sessionIdToUserIdMap.put(session.getId(), userId); // 便于获取当前session的userId
    }

    public WebSocketSession getSessionByUserId(String userId) {
        return userIdToSessionMap.get(userId);
    }

    public String getUserIdBySession(WebSocketSession session) {
        return sessionIdToUserIdMap.get(session.getId());
    }

    public void removeSession(WebSocketSession session) {
        userIdToSessionMap.remove(sessionIdToUserIdMap.get(session.getId()));
        sessionIdToUserIdMap.remove(session.getId());
    }

    public void sendMessageBySession(WebSocketSession session, Object message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (Exception e) {
            log.error("信息发送失败，对应的用户为：{}，对应的session为{}",
                    this.getUserIdBySession(session), session.getId());
        }
    }

    public Boolean isUserOline(String userId) {
        return userIdToSessionMap.get(userId) != null && userIdToSessionMap.get(userId).isOpen();
    }


    public void sendMessageToUser(String sendToUserId, Object message) throws Exception {
        try {
            WebSocketSession session = getSessionByUserId(sendToUserId);
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            }
        } catch (Exception e) {
            log.error("信息给用户失败");
        }
    }
}

