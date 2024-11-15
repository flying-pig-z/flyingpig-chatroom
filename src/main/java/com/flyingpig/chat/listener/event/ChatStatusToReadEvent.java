package com.flyingpig.chat.listener.event;

import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ChatStatusToReadEvent extends ApplicationEvent {

    List<ChatRespMessage> chatMessages;

    public ChatStatusToReadEvent(Object source, List<ChatRespMessage> chatMessages) {
        super(source);
        this.chatMessages = chatMessages;
    }

    public List<ChatRespMessage> getChatMessages() {
        return chatMessages;
    }

}
