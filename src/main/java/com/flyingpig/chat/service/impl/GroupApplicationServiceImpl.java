package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.constant.ApplyStatus;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.GroupApplicationInfo;
import com.flyingpig.chat.dataobject.eneity.GroupApplication;
import com.flyingpig.chat.dataobject.eneity.GroupRoomMembers;
import com.flyingpig.chat.dataobject.eneity.PrivateRoom;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.*;
import com.flyingpig.chat.service.IGroupApplicationService;
import com.flyingpig.chat.util.UserIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 申请表 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-07
 */
@Service
public class GroupApplicationServiceImpl extends ServiceImpl<GroupApplicationMapper, GroupApplication> implements IGroupApplicationService {

    @Autowired
    RoomMapper roomMapper;

    @Autowired
    PrivateRoomMapper privateRoomMapper;

    @Autowired
    GroupRoomMapper groupRoomMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    GroupRoomMembersMapper groupRoomMembersMapper;

    @Override
    public Boolean publishGroupApplication(Long roomId, String applyMsg) {
        // 检查聊天室有无存在
        if (groupRoomMapper.selectById(roomId) == null) {
            throw new RuntimeException("聊天室不存在");
        }

        // 检查聊天室有无添加
        List<GroupRoomMembers> groupRoomMembersList = groupRoomMembersMapper.selectList(new LambdaQueryWrapper<GroupRoomMembers>()
                .eq(GroupRoomMembers::getGroupRoomId, roomId));
        for (GroupRoomMembers groupRoomMembers : groupRoomMembersList) {
            if (groupRoomMembers.getUserId().toString().equals(UserIdContext.getUserId())) {
                throw new RuntimeException("聊天室已添加，不可重复添加");
            }
        }
        // 进行聊天室的添加
        return this.save(new GroupApplication()
                .setApplyUser(Long.parseLong(UserIdContext.getUserId())).setApplyMsg(applyMsg)
                .setApplyRoom(roomId).setApplyTime(LocalDateTime.now()).setStatus(ApplyStatus.UNKONWN)
                .setApplyTime(LocalDateTime.now())
                .setAuditUser(groupRoomMapper.selectById(roomId).getOwnerId()));
    }

    @Override
    public List<GroupApplicationInfo> selectSendApplication() {
        List<GroupApplication> applicationInfos = this.list(
                new LambdaQueryWrapper<GroupApplication>().eq(GroupApplication::getApplyUser, UserIdContext.getUserId()));
        List<GroupApplicationInfo> applicationInfoList = new ArrayList<>();
        for (GroupApplication application : applicationInfos) {
            GroupApplicationInfo applicationInfo = new GroupApplicationInfo();
            applicationInfo.setApplicationId(application.getId()).setApplyMsg(application.getApplyMsg())
                    .setApplyTime(application.getApplyTime()).setStatus(application.getStatus());
            User applyUser = userMapper.selectById(application.getApplyUser());
            applicationInfo.setApplyUserName(applyUser.getUsername())
                    .setApplyUserAvatar(applyUser.getAvatar())
                    .setRoomName(getRoomNameByRoomId(application.getApplyRoom()));
            applicationInfoList.add(applicationInfo);
        }
        return applicationInfoList;
    }

    private String getRoomNameByRoomId(Long roomId) {
        if (roomMapper.selectById(roomId).getType().shortValue() == RoomType.PRIVATE_ROOM.shortValue()) {
            PrivateRoom privateRoom = privateRoomMapper.selectById(roomId);
            if (privateRoom.getUserIdA().toString().equals(UserIdContext.getUserId())) {
                return userMapper.selectById(privateRoom.getUserIdB()).getUsername();
            } else {
                return userMapper.selectById(privateRoom.getUserIdA()).getUsername();
            }
        } else {
            return groupRoomMapper.selectById(roomId).getName();
        }
    }

    @Override
    public Boolean judgeApplication(Long applicationId, Byte status) {
        GroupApplication groupApplication = this.getById(applicationId);
        if (!groupApplication.getAuditUser().toString().equals(UserIdContext.getUserId())) {
            throw new RuntimeException("你没有审核该申请的权限");
        }
        if (status.equals(ApplyStatus.YES)) {
            // 如果用户还没加入群聊就进行添加
            if(null == groupRoomMembersMapper.selectOne(new LambdaQueryWrapper<GroupRoomMembers>()
                    .eq(GroupRoomMembers::getUserId, groupApplication.getApplyUser()).eq(GroupRoomMembers::getGroupRoomId, groupApplication.getApplyRoom()))){
                groupRoomMembersMapper.insert(new GroupRoomMembers().setUserId(groupApplication.getApplyUser())
                        .setGroupRoomId(groupApplication.getApplyRoom()));
            }
        }
        return updateById(new GroupApplication().setId(applicationId).setStatus(status));
    }

    @Override
    public List<GroupApplicationInfo> selectReceiveApplication() {
        List<GroupApplication> applicationInfos = this.list(
                new LambdaQueryWrapper<GroupApplication>().eq(GroupApplication::getAuditUser, UserIdContext.getUserId()));
        List<GroupApplicationInfo> applicationInfoList = new ArrayList<>();
        for (GroupApplication application : applicationInfos) {
            GroupApplicationInfo applicationInfo = new GroupApplicationInfo();
            applicationInfo.setApplicationId(application.getId()).setApplyMsg(application.getApplyMsg())
                    .setApplyTime(application.getApplyTime()).setStatus(application.getStatus());
            User applyUser = userMapper.selectById(application.getApplyUser());
            applicationInfo.setApplyUserName(applyUser.getUsername())
                    .setApplyUserAvatar(applyUser.getAvatar())
                    .setRoomName(getRoomNameByRoomId(application.getApplyRoom()));
            applicationInfoList.add(applicationInfo);
        }
        return applicationInfoList;
    }


}
