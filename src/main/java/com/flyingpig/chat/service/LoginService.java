package com.flyingpig.chat.service;

import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.dto.request.EmailRegisterReq;
import com.flyingpig.chat.dataobject.dto.request.LoginReq;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    Result login(LoginReq loginReq);

    Result logout(String userId);


    void registerUser(EmailRegisterReq emailRegisterReq);

    void sendVerificationCode(String email);
}
