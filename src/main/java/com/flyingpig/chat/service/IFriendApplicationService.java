package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.dto.response.FriendApplicationInfo;
import com.flyingpig.chat.dataobject.eneity.FriendApplication;

import java.util.List;

/**
 * <p>
 * 好友/私聊申请表 服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-12-01
 */
public interface IFriendApplicationService extends IService<FriendApplication> {

    Boolean publishFriendApplication(Long roomId, String applyMsg);

    List<FriendApplicationInfo> selectSendApplication();

    List<FriendApplicationInfo> selectReceiveApplication();

    Boolean judgeApplication(Long applicationId, Byte status);
}
