package com.flyingpig.chat.dataobject.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class RoomInfo {
    private Long roomId;
    private String roomName;
    private Byte type;
}
