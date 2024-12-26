package com.queryx.recruiting_website.vo;

import lombok.Data;

@Data
public class UserCompanyVo {
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
     * 0代表学生用户,1代表公司用户
     */
    private String userRole;
    /**
     * 头像
     */
    private String userAvatar;
}
