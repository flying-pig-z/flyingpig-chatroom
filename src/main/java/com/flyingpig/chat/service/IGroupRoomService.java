package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.dto.response.RoomInfo;
import com.flyingpig.chat.dataobject.dto.response.RoomWithReadMessage;
import com.flyingpig.chat.dataobject.eneity.GroupRoom;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
public interface IGroupRoomService extends IService<GroupRoom> {

    List<RoomWithReadMessage> listUserGroupRoomWithReadMessage();

    List<GroupRoom> search(String name);

    List<RoomInfo> listUserGroupRoom();

    Boolean addGroupRoom(String name, String introduce);
}
