package com.flyingpig.chat.listener.event;

import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;
import org.springframework.context.ApplicationEvent;

public class ChatMessageEvent extends ApplicationEvent {
    private final ChatRespMessage respMsg;

    private final Boolean isRead;

    private final Long sendToUserId;

    public ChatMessageEvent(Object source, ChatRespMessage respMsg, Boolean isRead, Long sendToUserId) {
        super(source);
        this.respMsg = respMsg;
        this.isRead = isRead;
        this.sendToUserId = sendToUserId;
    }

    public ChatRespMessage getRespMsg() {
        return respMsg;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public Long getSendToUserId() {
        return sendToUserId;
    }
}
