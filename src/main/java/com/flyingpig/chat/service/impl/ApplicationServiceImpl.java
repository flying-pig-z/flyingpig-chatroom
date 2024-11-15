package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.constant.ApplyStatus;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.ApplicationInfo;
import com.flyingpig.chat.dataobject.eneity.Application;
import com.flyingpig.chat.dataobject.eneity.PrivateRoom;
import com.flyingpig.chat.dataobject.eneity.User;
import com.flyingpig.chat.mapper.*;
import com.flyingpig.chat.service.IApplicationService;
import com.flyingpig.chat.util.UserContext;
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
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements IApplicationService {

    @Autowired
    RoomMapper roomMapper;

    @Autowired
    PrivateRoomMapper privateRoomMapper;

    @Autowired
    GroupRoomMapper groupRoomMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public Boolean publishApplication(Long roomId) {
        Application application = new Application()
                .setApplyUser(Long.parseLong(UserContext.getUser()))
                .setRoomId(roomId).setApplyTime(LocalDateTime.now()).setStatus(ApplyStatus.UNKONWN);
        if (roomMapper.selectById(roomId).getType().shortValue() == RoomType.PRIVATE_ROOM.shortValue()) {
            PrivateRoom privateRoom = privateRoomMapper.selectById(roomId);
            if (privateRoom.getUserIdA().toString().equals(UserContext.getUser())) {
                application.setApplyUser(privateRoom.getUserIdB());
            } else {
                application.setApplyUser(privateRoom.getUserIdA());
            }
        } else {
            application.setApplyUser(groupRoomMapper.selectById(roomId).getOwnerId());
        }
        this.save(application);
        return null;
    }

    @Override
    public List<ApplicationInfo> selectSendApplication() {
        List<Application> applicationInfos = this.list(
                new LambdaQueryWrapper<Application>().eq(Application::getApplyUser, UserContext.getUser()));
        List<ApplicationInfo> applicationInfoList = new ArrayList<>();
        for (Application application : applicationInfos){
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.setApplicationId(application.getId())
                    .setApplyTime(application.getApplyTime()).setStatus(application.getStatus());
            User applyUser = userMapper.selectById(application.getApplyUser());
            applicationInfo.setApplyUserName(applyUser.getUsername())
                    .setApplyUserAvatar(applyUser.getAvatar())
                    .setRoomName(getRoomNameByRoomId(application.getRoomId()));
            applicationInfoList.add(applicationInfo);
        }
        return applicationInfoList;
    }

    private String getRoomNameByRoomId(Long roomId){
        if (roomMapper.selectById(roomId).getType().shortValue() == RoomType.PRIVATE_ROOM.shortValue()) {
            PrivateRoom privateRoom = privateRoomMapper.selectById(roomId);
            if (privateRoom.getUserIdA().toString().equals(UserContext.getUser())) {
                return userMapper.selectById(privateRoom.getUserIdB()).getUsername();
            } else {
                return userMapper.selectById(privateRoom.getUserIdA()).getUsername();
            }
        } else {
            return groupRoomMapper.selectById(roomId).getName();
        }
    }
    @Override
    public void judgeApplication(Long applicationId, Byte status) {
        updateById(new Application().setId(applicationId).setStatus(status));
    }

    @Override
    public List<ApplicationInfo> selectReceiveApplication() {
        List<Application> applicationInfos = this.list(
                new LambdaQueryWrapper<Application>().eq(Application::getAuditUser, UserContext.getUser()));
        List<ApplicationInfo> applicationInfoList = new ArrayList<>();
        for (Application application : applicationInfos){
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.setApplicationId(application.getId())
                    .setApplyTime(application.getApplyTime()).setStatus(application.getStatus());
            User applyUser = userMapper.selectById(application.getApplyUser());
            applicationInfo.setApplyUserName(applyUser.getUsername())
                    .setApplyUserAvatar(applyUser.getAvatar())
                    .setRoomName(getRoomNameByRoomId(application.getRoomId()));
            applicationInfoList.add(applicationInfo);
        }
        return applicationInfoList;
    }
}
