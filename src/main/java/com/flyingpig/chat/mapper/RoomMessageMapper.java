package com.flyingpig.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyingpig.chat.dataobject.eneity.RoomMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 聊天室聊天消息 Mapper 接口
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
public interface RoomMessageMapper extends BaseMapper<RoomMessage> {



    @Select("SELECT room_message.id, room_message.room_id, room_message.send_user_id, " +
            "room_message.content, room_message.send_time FROM room_message " +
            "JOIN msg_status ON room_message.id = msg_status.msg_id WHERE room_message.room_id = #{roomId} and msg_status.is_read = 1")
    List<RoomMessage> selectHistoryReadMsg(@Param("roomId") Long roomId);
}
