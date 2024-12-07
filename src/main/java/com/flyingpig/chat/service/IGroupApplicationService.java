package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.dto.response.GroupApplicationInfo;
import com.flyingpig.chat.dataobject.eneity.GroupApplication;

import java.util.List;

/**
 * <p>
 * 申请表 服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-07
 */
public interface IGroupApplicationService extends IService<GroupApplication> {

    Boolean publishGroupApplication(Long roomId, String applyMsg);

    List<GroupApplicationInfo> selectSendApplication();

    Boolean judgeApplication(Long applicationId, Byte status);

    List<GroupApplicationInfo> selectReceiveApplication();
}
