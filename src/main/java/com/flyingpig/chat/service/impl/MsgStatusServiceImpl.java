package com.flyingpig.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.chat.dataobject.eneity.MsgStatus;
import com.flyingpig.chat.mapper.MsgStatusMapper;
import com.flyingpig.chat.service.IMsgStatusService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 私聊消息状态（已读未读） 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-04
 */
@Service
public class MsgStatusServiceImpl extends ServiceImpl<MsgStatusMapper, MsgStatus> implements IMsgStatusService {

}
