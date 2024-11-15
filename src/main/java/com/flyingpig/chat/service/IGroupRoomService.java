package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.dto.response.RoomInfo;
import com.flyingpig.chat.dataobject.dto.response.RoomSession;
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

    List<RoomSession> listUserGroupRoomSession();

    List<GroupRoom> search(String name);

    List<RoomInfo> listUserGroupRoom();
}
