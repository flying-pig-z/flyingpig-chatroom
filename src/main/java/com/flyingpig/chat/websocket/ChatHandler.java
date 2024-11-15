package com.flyingpig.chat.websocket;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.listener.event.ChatMessageEvent;
import com.flyingpig.chat.listener.event.ChatStatusToReadEvent;
import com.flyingpig.chat.service.IGroupRoomMembersService;
import com.flyingpig.chat.service.IPrivateRoomService;
import com.flyingpig.chat.websocket.cache.MessageCacheManager;
import com.flyingpig.chat.websocket.message.req.ChatReqMessage;
import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;
import com.flyingpig.chat.websocket.message.resp.WebSocketRespCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {


    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        ChatHandler.objectMapper = objectMapper;
    }


    private static WebSocketSessionManager sessionManager;

    @Autowired
    public void setSessionManager(WebSocketSessionManager sessionManager) {
        ChatHandler.sessionManager = sessionManager;
    }

    private static MessageCacheManager messageCacheManager;

    @Autowired
    public void setMessageCacheManager(MessageCacheManager messageCacheManager) {
        ChatHandler.messageCacheManager = messageCacheManager;
    }

    private static IPrivateRoomService privateRoomService;

    @Autowired
    public void setPrivateRoomService(IPrivateRoomService privateRoomService) {
        ChatHandler.privateRoomService = privateRoomService;
    }

    private static IGroupRoomMembersService groupRoomMembersService;

    @Autowired
    public void setGroupRoomMembersService(IGroupRoomMembersService groupRoomMembersService) {
        ChatHandler.groupRoomMembersService = groupRoomMembersService;
    }

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        ChatHandler.eventPublisher = eventPublisher;
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理接收到的消息
        log.debug("\nReceived message: {}", message.getPayload());

        // 解析消息内容
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());

        // 检查消息格式
        if (!jsonNode.has("type")) {
            sessionManager.sendMessageBySession(session, Result.error(WebSocketRespCode.PARAMETER_ERROR, "消息类型错误"));
            return;
        }
        // 处理不同类型的消息
        switch (jsonNode.get("type").asText()) {
            case "single":
                processSingleMessage(session, jsonNode);
                break;
            case "group":
                processGroupMessage(session, jsonNode);
                break;
            default:
                sessionManager.sendMessageBySession(session, Result.error(WebSocketRespCode.PARAMETER_ERROR, "消息类型错误"));
        }
    }


    private ChatRespMessage parseReqToResp(JsonNode jsonNode, WebSocketSession session) throws Exception {
        // 将请求消息数据封装成发送消息数据
        ChatReqMessage chatReqMessage = objectMapper.treeToValue(jsonNode, ChatReqMessage.class);
        ChatRespMessage chatRespMessage = new ChatRespMessage();
        BeanUtils.copyProperties(chatReqMessage, chatRespMessage);
        chatRespMessage.setSendUserId(Long.parseLong(sessionManager.getUserIdBySession(session)));
        chatRespMessage.setId(IdWorker.getId());
        chatRespMessage.setSendTime(LocalDateTime.now());
        return chatRespMessage;
    }

    private void processSingleMessage(WebSocketSession session, JsonNode jsonNode) {
        try {
            ChatRespMessage respMsg = parseReqToResp(jsonNode, session);
            // 判断发送对象
            Long sendToUserId = privateRoomService.getSendToUserId(respMsg);
            if (sendToUserId == null) {
                // 如果为空，说明没有找到对应的聊天室或者对应聊天室没有该成员，返回报错信息
                log.error("没有找到对应的聊天室 " + respMsg.getRoomId());
                sessionManager.sendMessageToUser(respMsg.getSendUserId().toString(), Result.error("没有找到对应的聊天室"));
                return;
            }
            if (sessionManager.isUserOline(sendToUserId.toString())) {
                // 如果不为空且对方是否在线则发送消息给对方
                sessionManager.sendMessageToUser(sendToUserId.toString(), Result.success(WebSocketRespCode.RECEIVED_MESSAGE, "接收消息", respMsg));
                // 异步加载进数据库
                eventPublisher.publishEvent(new ChatMessageEvent(this, respMsg, true, sendToUserId));
            } else {
                // 对方不在线，将消息加载到缓存
                messageCacheManager.saveUnreadMsgToRedis(sendToUserId, respMsg);
                log.info("对方不在线，消息已保存到缓存");
                // 异步加载进数据库
                eventPublisher.publishEvent(new ChatMessageEvent(this, respMsg, false, sendToUserId));
            }
            // 通知成功发送消息
            sessionManager.sendMessageBySession(session, Result.success(WebSocketRespCode.SEND_SECCESS, "消息发送成功"));
        } catch (Exception e) {
            // 记录日志
            log.error("单聊信息发送失败: {}", e.getMessage(), e);
            sessionManager.sendMessageBySession(session, Result.error("单聊信息发送失败"));
        }
    }

    private void processGroupMessage(WebSocketSession session, JsonNode jsonNode) {
        try {
            ChatRespMessage respMsg = parseReqToResp(jsonNode, session);
            // 获取群聊其他成员
            List<Long> userIdList = groupRoomMembersService.listGroupMemsExpSelf(respMsg);

            for (Long sendToUserId : userIdList) {
                if (sessionManager.isUserOline(sendToUserId.toString())) {
                    // 如果不为空且对方是否在线
                    sessionManager.sendMessageToUser(sendToUserId.toString(), Result.success(WebSocketRespCode.RECEIVED_MESSAGE, "接收消息", respMsg));
                    log.info("发送成功");
                    // 异步加载进数据库
                    eventPublisher.publishEvent(new ChatMessageEvent(this, respMsg, true, sendToUserId));
                } else {
                    // 对方不在线，将消息加载到缓存
                    messageCacheManager.saveUnreadMsgToRedis(sendToUserId, respMsg);
                    log.info("对方不在线，消息已保存到缓存");
                    // 异步加载进数据库
                    eventPublisher.publishEvent(new ChatMessageEvent(this, respMsg, false, sendToUserId));
                }
            }
        } catch (Exception e) {
            // 记录日志
            log.error("群聊信息发送失败: {}", e.getMessage(), e);
            sessionManager.sendMessageBySession(session, Result.error("群聊信息发送异常，用户不在群聊或发送信息不完整"));
        }
    }


    // 连接建立时的逻辑
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取userId
        String userId = (String) session.getAttributes().get("userId");
        // 保存信息 发送信息通知用户 记录日志
        sessionManager.addSession(userId, session);
        log.info("用户建立连接, 用户ID为: {}, 会话ID为: {}", userId, session.getId());
        // 查询并发送未读消息，随后清空
        List<ChatRespMessage> chatMessages = messageCacheManager.getUnreadMessageByUserId(sessionManager.getUserIdBySession(session));
        sessionManager.sendMessageBySession(session, Result.success(WebSocketRespCode.RETURN_UNREAD, "已查询未读数据返回", chatMessages));
        // 异步修改数据库中数据状态
        eventPublisher.publishEvent(new ChatStatusToReadEvent(this, chatMessages));

    }


    // 处理传输错误
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 处理错误，发送错误消息
        if (session.isOpen()) {
            sessionManager.sendMessageBySession(session, Result.error(exception.getMessage()));
        }
        // 记录日志
        log.error("用户ID为 {} 的用户传输过程中抛出异常： {}", sessionManager.getUserIdBySession(session), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 删除保存的信息，打印日志
        sessionManager.removeSession(session);
        log.info("用户断开连接, 用户ID为: {}, 会话ID为: {}", sessionManager.getUserIdBySession(session), session.getId());
    }
}