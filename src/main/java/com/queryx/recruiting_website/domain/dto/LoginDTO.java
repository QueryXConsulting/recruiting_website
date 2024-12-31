package com.queryx.recruiting_website.domain.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginDTO {
    // 可能是手机号也可能是邮箱
    private String username;
    private String userPassword;
    private String userRole;
    // TODO 以后用到
    private String userCaptcha;
}
