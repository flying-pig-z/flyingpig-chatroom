package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.eneity.RoomMessage;
import com.flyingpig.chat.listener.event.ChatMessageEvent;

import java.util.List;

/**
 * <p>
 * 聊天室聊天消息 服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
public interface IRoomMessageService extends IService<RoomMessage> {

    List<RoomMessage> listRoomMessage(Long roomId);


    void saveChatMessageToDB(ChatMessageEvent event);
}
