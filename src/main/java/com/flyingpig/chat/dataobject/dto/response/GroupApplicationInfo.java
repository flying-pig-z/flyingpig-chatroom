package com.flyingpig.chat.dataobject.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class GroupApplicationInfo {

    private Long applicationId;

    private String applyUserName;

    private String applyUserAvatar;

    private String roomName;

    private String applyMsg;

    private LocalDateTime applyTime;

    private Byte status;
}
