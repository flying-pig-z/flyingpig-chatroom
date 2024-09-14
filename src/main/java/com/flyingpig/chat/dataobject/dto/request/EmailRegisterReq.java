package com.flyingpig.chat.dataobject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisterReq {
    public String email;
    public String verificationCode;
    //用户名
    private String username;
    //密码
    public String password;

}