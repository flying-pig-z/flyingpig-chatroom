package com.flyingpig.chat.websocket.message.resp;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatRespMessage {
    private Long id;

    private Long roomId;

    private Long sendUserId;

    private String content;

    private LocalDateTime sendTime;

}
