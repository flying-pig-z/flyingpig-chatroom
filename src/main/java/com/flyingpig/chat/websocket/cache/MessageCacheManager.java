package com.flyingpig.chat.websocket.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.flyingpig.chat.dataobject.constant.RedisConstants.MESSAGE_LIST_KEY;
import static com.flyingpig.chat.dataobject.constant.RedisConstants.UNREAD_MESSAGE_KEY;

@Component
@Slf4j
public class MessageCacheManager {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    ObjectMapper objectMapper;

    public void saveUnreadMsgToRedis(Long sendToUserId, ChatRespMessage respMessage) throws JsonProcessingException {
        stringRedisTemplate.opsForZSet().add(
                UNREAD_MESSAGE_KEY + sendToUserId,
                objectMapper.writeValueAsString(respMessage),  // 将 chatMessage 序列化为 JSON 字符串
                respMessage.getSendTime().getNano()  // 使用毫秒排序
        );
        stringRedisTemplate.opsForZSet().add(
                MESSAGE_LIST_KEY + sendToUserId,
                objectMapper.writeValueAsString(respMessage),  // 将 chatMessage 序列化为 JSON 字符串
                respMessage.getSendTime().getNano()  // 使用毫秒排序
        );

    }

    public List<ChatRespMessage> getUnreadMessageByUserId(String userIdBySession) {
        // 获取 Redis 中的未读消息
        Set<String> unreadMessages = stringRedisTemplate.opsForZSet().range(UNREAD_MESSAGE_KEY + userIdBySession, 0, -1);
        List<ChatRespMessage> messages = new ArrayList<>();

        // 反序列化消息并添加到列表中
        if (unreadMessages != null) {
            for (String messageJson : unreadMessages) {
                try {
                    ChatRespMessage message = objectMapper.readValue(messageJson, ChatRespMessage.class);
                    messages.add(message);
                } catch (JsonProcessingException e) {
                    log.error("反序列化未读消息时发生异常: {}", e.getMessage(), e);
                }
            }

            // 清空未读消息
            stringRedisTemplate.opsForZSet().removeRange(UNREAD_MESSAGE_KEY + userIdBySession, 0, -1);
            log.info("清空用户 {} 的未读消息", userIdBySession);
        }

        return messages;
    }


}
