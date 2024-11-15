package com.flyingpig.chat.websocket.message.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReqMessage {
    private String type;  // 消息类型,single--单聊,group--群聊
    private Long roomId; // 房间Id
    private String content;  // 公共字段，比如消息内容
}

