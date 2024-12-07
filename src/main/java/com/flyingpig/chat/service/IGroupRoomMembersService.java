package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.eneity.GroupRoomMembers;
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
public interface IGroupRoomMembersService extends IService<GroupRoomMembers> {

    List<Long> listGroupMemsExpSelf(ChatRespMessage respMsg);

    List<Long> listGroupRoomMembers(Long groupRoomId);
}
