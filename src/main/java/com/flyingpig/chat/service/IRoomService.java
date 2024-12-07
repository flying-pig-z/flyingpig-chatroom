package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.dto.response.UserInfo;
import com.flyingpig.chat.dataobject.eneity.Room;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
public interface IRoomService extends IService<Room> {

    List<UserInfo> listMemberList(Long roomId);
}
