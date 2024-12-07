package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.eneity.GroupRoomMembers;
import com.flyingpig.chat.mapper.GroupRoomMembersMapper;
import com.flyingpig.chat.service.IGroupRoomMembersService;
import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
@Service
public class GroupRoomMembersServiceImpl extends ServiceImpl<GroupRoomMembersMapper, GroupRoomMembers> implements IGroupRoomMembersService {

    @Override
    public List<Long> listGroupMemsExpSelf(ChatRespMessage respMsg) {
        // 查询群聊成员列表
        List<Long> userIdList = this.list(
                        new LambdaQueryWrapper<GroupRoomMembers>()
                                .eq(GroupRoomMembers::getGroupRoomId, respMsg.getRoomId())
                ).stream()
                .map(GroupRoomMembers::getUserId)
                .collect(Collectors.toList());

        // 判断发送者是否在群聊中
        if (!userIdList.contains(respMsg.getSendUserId())) {
            // 如果发送者不在群聊中，返回错误
            throw new IllegalArgumentException("用户不在该群聊中");
        }

        // 如果发送者在群聊中，过滤掉发送者的 ID
        userIdList = userIdList.stream()
                .filter(id -> !id.equals(respMsg.getSendUserId())) // 移除发送者
                .collect(Collectors.toList());

        return userIdList;
    }

    @Override
    public List<Long> listGroupRoomMembers(Long groupRoomId) {
        return this.list(new LambdaQueryWrapper<GroupRoomMembers>()
                                .eq(GroupRoomMembers::getGroupRoomId, groupRoomId))
                .stream()
                .map(GroupRoomMembers::getUserId)
                .collect(Collectors.toList());
    }
}
