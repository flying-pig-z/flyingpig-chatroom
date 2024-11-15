package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.dto.response.RoomInfo;
import com.flyingpig.chat.dataobject.dto.response.RoomSession;
import com.flyingpig.chat.dataobject.eneity.PrivateRoom;
import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
public interface IPrivateRoomService extends IService<PrivateRoom> {

    List<RoomSession> listUserPrivateRoomSession();

    Long getSendToUserId(ChatRespMessage chatRespMessage);

    List<RoomInfo> listUserPrivateRoom();
}
