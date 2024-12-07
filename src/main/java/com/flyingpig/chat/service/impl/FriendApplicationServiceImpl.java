package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.constant.ApplyStatus;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.FriendApplicationInfo;
import com.flyingpig.chat.dataobject.eneity.*;
import com.flyingpig.chat.mapper.*;
import com.flyingpig.chat.service.IFriendApplicationService;
import com.flyingpig.chat.util.UserIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 好友/私聊申请表 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-12-01
 */
@Service
public class FriendApplicationServiceImpl extends ServiceImpl<FriendApplicationMapper, FriendApplication> implements IFriendApplicationService {

    @Autowired
    PrivateRoomMapper privateRoomMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RoomMapper roomMapper;

    @Autowired
    GroupRoomMapper groupRoomMapper;

    @Override
    public Boolean publishFriendApplication(Long userId, String applyMsg) {
        // 检查好友有无添加
        List<Long> friendIdList = new ArrayList<>();
        for (PrivateRoom room : this.privateRoomMapper.selectList(
                new LambdaQueryWrapper<PrivateRoom>().eq(PrivateRoom::getUserIdA, UserIdContext.getUserId()))) {
            friendIdList.add(room.getUserIdB());
        }
        for (PrivateRoom room : this.privateRoomMapper.selectList(
                new LambdaQueryWrapper<PrivateRoom>().eq(PrivateRoom::getUserIdB, UserIdContext.getUserId()))) {
            friendIdList.add(room.getUserIdB());
        }
        if (friendIdList.contains(userId)) {
            throw new RuntimeException("以添加次用户，不可重复添加");
        }
        // 保存申请
        return this.save(new FriendApplication()
                .setApplyUser(Long.parseLong(UserIdContext.getUserId())).setApplyMsg(applyMsg)
                .setApplyTime(LocalDateTime.now()).setStatus(ApplyStatus.UNKONWN)
                .setAuditUser(userId));
    }

    @Override
    public List<FriendApplicationInfo> selectSendApplication() {
        List<FriendApplication> applicationInfos = this.list(
                new LambdaQueryWrapper<FriendApplication>().eq(FriendApplication::getApplyUser, UserIdContext.getUserId()));
        List<FriendApplicationInfo> applicationInfoList = new ArrayList<>();
        for (FriendApplication application : applicationInfos) {
            FriendApplicationInfo applicationInfo = new FriendApplicationInfo();
            applicationInfo.setApplicationId(application.getId()).setApplyMsg(application.getApplyMsg())
                    .setApplyTime(application.getApplyTime()).setStatus(application.getStatus());
            User applyUser = userMapper.selectById(application.getApplyUser());
            applicationInfo.setApplyUserName(applyUser.getUsername())
                    .setApplyUserAvatar(applyUser.getAvatar());
            applicationInfoList.add(applicationInfo);
        }
        return applicationInfoList;
    }

    @Override
    public List<FriendApplicationInfo> selectReceiveApplication() {
        List<FriendApplication> applicationInfos = this.list(
                new LambdaQueryWrapper<FriendApplication>().eq(FriendApplication::getAuditUser, UserIdContext.getUserId()));
        List<FriendApplicationInfo> applicationInfoList = new ArrayList<>();
        for (FriendApplication application : applicationInfos) {
            FriendApplicationInfo applicationInfo = new FriendApplicationInfo();
            applicationInfo.setApplicationId(application.getId()).setApplyMsg(application.getApplyMsg())
                    .setApplyTime(application.getApplyTime()).setStatus(application.getStatus());
            User applyUser = userMapper.selectById(application.getApplyUser());
            applicationInfo.setApplyUserName(applyUser.getUsername())
                    .setApplyUserAvatar(applyUser.getAvatar());
            applicationInfoList.add(applicationInfo);
        }
        return applicationInfoList;
    }

    @Override
    public Boolean judgeApplication(Long applicationId, Byte status) {
        FriendApplication friendApplication = this.getById(applicationId);
        if (!friendApplication.getAuditUser().toString().equals(UserIdContext.getUserId())) {
            throw new RuntimeException("你没有审核该申请的权限");
        }
        if (status.equals(ApplyStatus.YES)) {
            // 如果用户还没加入群聊就进行添加
            List<Long> friendIdList = new ArrayList<>();
            for (PrivateRoom room : this.privateRoomMapper.selectList(
                    new LambdaQueryWrapper<PrivateRoom>().eq(PrivateRoom::getUserIdA, UserIdContext.getUserId()))) {
                friendIdList.add(room.getUserIdB());
            }
            for (PrivateRoom room : this.privateRoomMapper.selectList(
                    new LambdaQueryWrapper<PrivateRoom>().eq(PrivateRoom::getUserIdB, UserIdContext.getUserId()))) {
                friendIdList.add(room.getUserIdB());
            }
            if (!friendIdList.contains(friendApplication.getApplyUser())) {
                Room room = new Room(null, LocalDateTime.now(), RoomType.PRIVATE_ROOM);
                roomMapper.insert(room);
                privateRoomMapper.insert(new PrivateRoom(room.getId(), friendApplication.getApplyUser(), friendApplication.getAuditUser()));
            }
        }
        return updateById(new FriendApplication().setId(applicationId).setStatus(status));
    }

}
