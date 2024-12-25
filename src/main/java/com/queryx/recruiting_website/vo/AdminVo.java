package com.queryx.recruiting_website.vo;

import lombok.Data;

@Data
public class AdminVo {

    /**
     * 管理员角色
     */
    private Long roleId;

    /**
     * 用户名称
     */
    private String adminName;

    /**
     * 账号
     */
    private String adminUsername;

    /**
     * 密码
     */
    private String adminPassword;

    /**
     * 头像
     */
    private String adminAvatar;




}
