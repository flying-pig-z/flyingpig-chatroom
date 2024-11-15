package com.flyingpig.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.chat.dataobject.dto.response.ApplicationInfo;
import com.flyingpig.chat.dataobject.eneity.Application;

import java.util.List;

/**
 * <p>
 * 申请表 服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-07
 */
public interface IApplicationService extends IService<Application> {

    Boolean publishApplication(Long roomId);

    List<ApplicationInfo> selectSendApplication();

    void judgeApplication(Long applicationId, Byte status);

    List<ApplicationInfo> selectReceiveApplication();
}
