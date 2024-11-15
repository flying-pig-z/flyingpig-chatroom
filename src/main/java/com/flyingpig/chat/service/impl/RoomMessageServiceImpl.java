package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.eneity.RoomMessage;
import com.flyingpig.chat.mapper.RoomMessageMapper;
import com.flyingpig.chat.service.IRoomMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 聊天室聊天消息 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
@Service
public class RoomMessageServiceImpl extends ServiceImpl<RoomMessageMapper, RoomMessage> implements IRoomMessageService {

    @Override
    public List<RoomMessage> listRoomMessage(Long roomId) {
        return list(new LambdaQueryWrapper<RoomMessage>()
                .eq(RoomMessage::getRoomId, roomId)
                .orderByAsc(RoomMessage::getSendTime));
    }
}
