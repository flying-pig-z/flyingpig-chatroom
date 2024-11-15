package com.flyingpig.chat.dataobject.dto.response;

import com.flyingpig.chat.dataobject.eneity.RoomMessage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RoomSession {
    private Long roomId;
    private String roomName;
    List<RoomMessage> historyMessage;
    private Byte type;
}
