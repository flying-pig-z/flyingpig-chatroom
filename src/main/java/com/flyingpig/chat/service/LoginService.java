package com.flyingpig.chat.service;

import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.eneity.User;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    Result login(User user);

    Result logout(String userId);
}
