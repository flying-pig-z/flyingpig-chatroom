package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.eneity.MsgStatus;
import com.flyingpig.chat.dataobject.eneity.RoomMessage;
import com.flyingpig.chat.listener.event.ChatMessageEvent;
import com.flyingpig.chat.mapper.MsgStatusMapper;
import com.flyingpig.chat.mapper.RoomMessageMapper;
import com.flyingpig.chat.service.IRoomMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    RoomMessageMapper roomMessageMapper;

    @Autowired
    MsgStatusMapper msgStatusMapper;


    @Override
    public List<RoomMessage> listRoomMessage(Long roomId) {
        return list(new LambdaQueryWrapper<RoomMessage>()
                .eq(RoomMessage::getRoomId, roomId)
                .orderByAsc(RoomMessage::getSendTime));
    }


    // 适配客户端
    @Override
    public void saveChatMessageToDB(ChatMessageEvent event) {
        // 这里可以调用数据库保存逻辑
        RoomMessage roomMessage = new RoomMessage();
        BeanUtils.copyProperties(event.getRespMsg(), roomMessage);
        roomMessageMapper.insert(roomMessage);
        MsgStatus msgStatus = new MsgStatus().setMsgId(roomMessage.getId())
                .setIsRead(event.getIsRead()).setSendToUserId(event.getSendToUserId());
        msgStatusMapper.insert(msgStatus);
    }
}
