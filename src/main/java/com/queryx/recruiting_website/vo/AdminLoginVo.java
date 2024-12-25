package com.queryx.recruiting_website.vo;

import lombok.Data;

@Data
public class AdminLoginVo {
    /**
     * 账号
     */
    private String adminUsername;
    /**
     * 密码
     */
    private String adminPassword;
}
