package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.UserInfo;
import com.flyingpig.chat.dataobject.eneity.GroupRoomMembers;
import com.flyingpig.chat.dataobject.eneity.Room;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.GroupRoomMembersMapper;
import com.flyingpig.chat.mapper.PrivateRoomMapper;
import com.flyingpig.chat.mapper.RoomMapper;
import com.flyingpig.chat.mapper.UserMapper;
import com.flyingpig.chat.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {


    @Autowired
    GroupRoomMembersMapper groupRoomMembersMapper;

    @Autowired
    PrivateRoomMapper privateRoomMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public List<UserInfo> listMemberList(Long roomId) {
        List<Long> userIdList = new ArrayList<>();
        if (this.baseMapper.selectById(roomId).getType().equals(RoomType.GROUP_ROOM)) {
            userIdList = groupRoomMembersMapper.selectList(new LambdaQueryWrapper<GroupRoomMembers>()
                            .eq(GroupRoomMembers::getGroupRoomId, roomId))
                    .stream()
                    .map(GroupRoomMembers::getUserId)
                    .collect(Collectors.toList());
        } else {
            userIdList.add(privateRoomMapper.selectById(roomId).getUserIdA());
            userIdList.add(privateRoomMapper.selectById(roomId).getUserIdB());
        }

        List<UserInfo> userInfoList = new ArrayList<>();
        for (User user : userMapper.selectBatchIds(userIdList)) {
            userInfoList.add(new UserInfo(user.getId(), user.getUsername(), user.getAvatar()));
        }
        return userInfoList;
    }
}
