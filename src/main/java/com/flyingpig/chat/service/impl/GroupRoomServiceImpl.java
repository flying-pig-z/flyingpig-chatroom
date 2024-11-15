package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.RoomInfo;
import com.flyingpig.chat.dataobject.dto.response.RoomSession;
import com.flyingpig.chat.dataobject.eneity.GroupRoom;
import com.flyingpig.chat.dataobject.eneity.GroupRoomMembers;
import com.flyingpig.chat.mapper.GroupRoomMapper;
import com.flyingpig.chat.mapper.GroupRoomMembersMapper;
import com.flyingpig.chat.mapper.RoomMessageMapper;
import com.flyingpig.chat.service.IGroupRoomService;
import com.flyingpig.chat.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
@Service
public class GroupRoomServiceImpl extends ServiceImpl<GroupRoomMapper, GroupRoom> implements IGroupRoomService {

    @Autowired
    private GroupRoomMembersMapper groupRoomMembersMapper;

    @Autowired
    RoomMessageMapper roomMessageMapper;

    @Override
    public List<RoomSession> listUserGroupRoomSession() {
        // 根据用户id查询私聊房间
        List<RoomSession> roomSessions = new ArrayList<>();
        List<GroupRoomMembers> groupRoomMembersList = groupRoomMembersMapper.selectList(
                new LambdaQueryWrapper<GroupRoomMembers>()
                        .eq(GroupRoomMembers::getUserId, UserContext.getUser()));
        for (GroupRoomMembers groupRoomMembers : groupRoomMembersList) {
            // 封装返回结果，其中roomId为房间id，roomName为对方用户名作为会话名
            roomSessions.add(new RoomSession()
                    .setType(RoomType.GROUP_ROOM)
                    .setRoomId(groupRoomMembers.getGroupRoomId())
                    .setRoomName(
                            Optional.ofNullable(this.getById(groupRoomMembers.getGroupRoomId())) // 使用 Optional 封装返回值
                                    .map(GroupRoom::getName)  // 如果非 null，则获取 name
                                    .orElse("Default Room Name") // 如果是 null，则使用默认值
                    )
                    .setHistoryMessage(roomMessageMapper.selectHistoryReadMsg(groupRoomMembers.getGroupRoomId()))
            );
        }
        return roomSessions;
    }

    @Override
    public List<GroupRoom> search(String name) {
        return this.list(new LambdaQueryWrapper<GroupRoom>().like(GroupRoom::getName, name));
    }

    @Override
    public List<RoomInfo> listUserGroupRoom() {
        // 根据用户id查询私聊房间
        List<RoomInfo> roomInfos = new ArrayList<>();
        List<GroupRoomMembers> groupRoomMembersList = groupRoomMembersMapper.selectList(
                new LambdaQueryWrapper<GroupRoomMembers>()
                        .eq(GroupRoomMembers::getUserId, UserContext.getUser()));
        for (GroupRoomMembers groupRoomMembers : groupRoomMembersList) {
            // 封装返回结果，其中roomId为房间id，roomName为对方用户名作为会话名
            roomInfos.add(new RoomInfo()
                    .setType(RoomType.GROUP_ROOM)
                    .setRoomId(groupRoomMembers.getGroupRoomId())
                    .setRoomName(
                            Optional.ofNullable(this.getById(groupRoomMembers.getGroupRoomId())) // 使用 Optional 封装返回值
                                    .map(GroupRoom::getName)  // 如果非 null，则获取 name
                                    .orElse("Default Room Name") // 如果是 null，则使用默认值
                    )
            );
        }
        return roomInfos;
    }

}
