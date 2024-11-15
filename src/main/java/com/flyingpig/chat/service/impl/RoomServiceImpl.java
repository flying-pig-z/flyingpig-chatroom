package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.eneity.Room;
import com.flyingpig.chat.mapper.RoomMapper;
import com.flyingpig.chat.service.IRoomService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {

}
