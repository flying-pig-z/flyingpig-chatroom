package com.flyingpig.chat.listener;

import com.flyingpig.chat.dataobject.eneity.MsgStatus;
import com.flyingpig.chat.dataobject.eneity.RoomMessage;
import com.flyingpig.chat.listener.event.ChatMessageEvent;
import com.flyingpig.chat.listener.event.ChatStatusToReadEvent;
import com.flyingpig.chat.mapper.MsgStatusMapper;
import com.flyingpig.chat.mapper.RoomMessageMapper;
import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ChatMessageEventListener {

    @Autowired
    RoomMessageMapper roomMessageMapper;

    @Autowired
    MsgStatusMapper msgStatusMapper;

    @Async
    @EventListener
    public void handleChatMessageEvent(ChatMessageEvent event) {
        try {
            // 这里可以调用数据库保存逻辑
            RoomMessage roomMessage = new RoomMessage();
            BeanUtils.copyProperties(event.getRespMsg(), roomMessage);
            roomMessageMapper.insert(roomMessage);
            MsgStatus msgStatus = new MsgStatus().setMsgId(roomMessage.getId())
                    .setIsRead(event.getIsRead()).setSendToUserId(event.getSendToUserId());
            msgStatusMapper.insert(msgStatus);
        } catch (Exception e) {
            log.error("消息保存到数据库失败: {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void handleChatStatusToReadEvent(ChatStatusToReadEvent event) {
        List<ChatRespMessage> chatRespMessageList = event.getChatMessages();
        try {// 这里可以调用数据库保存逻辑
            for (ChatRespMessage chatRespMessage : chatRespMessageList) {
                MsgStatus msgStatus = new MsgStatus().setMsgId(chatRespMessage.getId()).setIsRead(true);
                msgStatusMapper.updateById(msgStatus);
            }
        } catch (Exception e) {
            log.error("消息保存到数据库失败: {}", e.getMessage(), e);
        }
    }
}
