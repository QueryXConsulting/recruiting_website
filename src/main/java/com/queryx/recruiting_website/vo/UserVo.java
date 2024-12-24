package com.queryx.recruiting_website.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVo {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;
    /**
     * 手机号
     */
    private String userPhone;
    /**
     * 密码
     */
    private String userPassword;

    /**
     * 头像
     */
    private String userAvatar;


}
