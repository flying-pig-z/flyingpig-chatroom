package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.constant.RoomType;
import com.flyingpig.chat.dataobject.dto.response.RoomInfo;
import com.flyingpig.chat.dataobject.dto.response.RoomWithReadMessage;
import com.flyingpig.chat.dataobject.eneity.PrivateRoom;
import com.flyingpig.chat.mapper.PrivateRoomMapper;
import com.flyingpig.chat.mapper.RoomMessageMapper;
import com.flyingpig.chat.mapper.UserMapper;
import com.flyingpig.chat.service.IPrivateRoomService;
import com.flyingpig.chat.util.UserIdContext;
import com.flyingpig.chat.websocket.message.resp.ChatRespMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
@Service
public class PrivateRoomServiceImpl extends ServiceImpl<PrivateRoomMapper, PrivateRoom> implements IPrivateRoomService {

    @Autowired
    private PrivateRoomMapper privateRoomMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    RoomMessageMapper roomMessageMapper;

    @Override
    public List<RoomWithReadMessage> listUserPrivateRoomWithReadMessage() {
        // 根据用户id查询私聊房间
        List<RoomWithReadMessage> roomSessions = new ArrayList<>();

        List<PrivateRoom> privateRooms1 = this.privateRoomMapper.selectList(
                new LambdaQueryWrapper<PrivateRoom>()
                        .eq(PrivateRoom::getUserIdA, UserIdContext.getUserId()));
        for (PrivateRoom room : privateRooms1) {
            // 封装返回结果，其中roomId为房间id，roomName为对方用户名作为会话名
            roomSessions.add(new RoomWithReadMessage()
                    .setType(RoomType.PRIVATE_ROOM).setRoomId(room.getId())
                    .setRoomName(userMapper.selectById(room.getUserIdB()).getUsername())
                    .setReadMessage(roomMessageMapper.selectHistoryReadMsg(room.getId())));
        }

        List<PrivateRoom> privateRooms2 = this.privateRoomMapper.selectList(
                new LambdaQueryWrapper<PrivateRoom>()
                        .eq(PrivateRoom::getUserIdB, UserIdContext.getUserId()));
        for (PrivateRoom room : privateRooms2) {
            // 封装返回结果，其中roomId为房间id，roomName为对方用户名作为会话名
            roomSessions.add(new RoomWithReadMessage()
                    .setType(RoomType.PRIVATE_ROOM).setRoomId(room.getId())
                    .setRoomName(userMapper.selectById(room.getUserIdA()).getUsername())
                    .setReadMessage(roomMessageMapper.selectHistoryReadMsg(room.getId())));
        }
        return roomSessions;
    }

    @Override
    public Long getSendToUserId(ChatRespMessage chatRespMessage) {
        // 根据 roomId 获取聊天室
        PrivateRoom privateRoom = privateRoomMapper.selectById(chatRespMessage.getRoomId());
        // 没有找到对应的聊天室
        if (privateRoom == null) {
            return null;
        }
        if (privateRoom.getUserIdA().equals(chatRespMessage.getSendUserId())) {
            return privateRoom.getUserIdB();
        } else if (privateRoom.getUserIdB().equals(chatRespMessage.getSendUserId())) {
            return privateRoom.getUserIdA();
        }
        // 该单聊聊天室没有该成员
        return null;
    }

    @Override
    public List<RoomInfo> listUserPrivateRoom() {
        // 根据用户id查询私聊房间
        List<RoomInfo> roomInfos = new ArrayList<>();

        List<PrivateRoom> privateRooms1 = this.privateRoomMapper.selectList(
                new LambdaQueryWrapper<PrivateRoom>()
                        .eq(PrivateRoom::getUserIdA, UserIdContext.getUserId()));
        for (PrivateRoom room : privateRooms1) {
            // 封装返回结果，其中roomId为房间id，roomName为对方用户名作为会话名
            roomInfos.add(new RoomInfo()
                    .setType(RoomType.PRIVATE_ROOM).setRoomId(room.getId())
                    .setRoomName(userMapper.selectById(room.getUserIdB()).getUsername()));
        }

        List<PrivateRoom> privateRooms2 = this.privateRoomMapper.selectList(
                new LambdaQueryWrapper<PrivateRoom>()
                        .eq(PrivateRoom::getUserIdB, UserIdContext.getUserId()));
        for (PrivateRoom room : privateRooms2) {
            // 封装返回结果，其中roomId为房间id，roomName为对方用户名作为会话名
            roomInfos.add(new RoomInfo()
                    .setType(RoomType.PRIVATE_ROOM).setRoomId(room.getId())
                    .setRoomName(userMapper.selectById(room.getUserIdA()).getUsername()));
        }
        return roomInfos;
    }
}
