package com.queryx.recruiting_website.domain.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 用户登录数据传输对象
 */
@Data
@ToString
public class LoginDTO {
    private String username;
    private String userPassword;
    private String userRole;
    private String userCaptcha;
}
