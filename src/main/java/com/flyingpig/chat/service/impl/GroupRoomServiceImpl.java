package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.RoomInfo;
import com.flyingpig.chat.dataobject.dto.response.RoomWithReadMessage;
import com.flyingpig.chat.dataobject.eneity.GroupRoom;
import com.flyingpig.chat.dataobject.eneity.GroupRoomMembers;
import com.flyingpig.chat.dataobject.eneity.Room;
import com.flyingpig.chat.mapper.*;
import com.flyingpig.chat.service.IGroupRoomService;
import com.flyingpig.chat.util.UserIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Autowired
    RoomMapper roomMapper;

    @Autowired
    PrivateRoomMapper privateRoomMapper;

    @Autowired
    GroupRoomMapper groupRoomMapper;

    @Override
    public List<RoomWithReadMessage> listUserGroupRoomWithReadMessage() {
        // 根据用户id查询私聊房间
        List<RoomWithReadMessage> roomWithReadMessageList = new ArrayList<>();
        List<GroupRoomMembers> groupRoomMembersList = groupRoomMembersMapper.selectList(
                new LambdaQueryWrapper<GroupRoomMembers>()
                        .eq(GroupRoomMembers::getUserId, UserIdContext.getUserId()));
        for (GroupRoomMembers groupRoomMembers : groupRoomMembersList) {
            // 封装返回结果，其中roomId为房间id，roomName为对方用户名作为会话名
            roomWithReadMessageList.add(new RoomWithReadMessage()
                    .setType(RoomType.GROUP_ROOM)
                    .setRoomId(groupRoomMembers.getGroupRoomId())
                    .setRoomName(
                            Optional.ofNullable(this.getById(groupRoomMembers.getGroupRoomId())) // 使用 Optional 封装返回值
                                    .map(GroupRoom::getName)  // 如果非 null，则获取 name
                                    .orElse("Default Room Name") // 如果是 null，则使用默认值
                    )
                    .setReadMessage(roomMessageMapper.selectHistoryReadMsg(groupRoomMembers.getGroupRoomId()))
            );
        }
        return roomWithReadMessageList;
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
                        .eq(GroupRoomMembers::getUserId, UserIdContext.getUserId()));
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

    @Override
    public Boolean addGroupRoom(String name, String introduce) {
        Room room = new Room(null, LocalDateTime.now(), RoomType.PRIVATE_ROOM);
        roomMapper.insert(room);
        groupRoomMapper.insert(new GroupRoom(room.getId(), Long.parseLong(UserIdContext.getUserId()), introduce, name));
        groupRoomMembersMapper.insert(new GroupRoomMembers(null, room.getId(), Long.parseLong(UserIdContext.getUserId())));
        return true;
    }

}
