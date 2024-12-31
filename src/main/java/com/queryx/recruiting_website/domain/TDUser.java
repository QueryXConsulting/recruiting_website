package com.queryx.recruiting_website.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TDUser)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDUser {
    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    /**
     * 简历id
     */
    private Long resumeId;
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
     * 0代表启用,1代表禁用
     */
    private String userStatus;
    /**
     * 头像
     */
    private String userAvatar;
    /**
     * 待面试数量
     */
    private Integer userInterviews;
    /**
     * 注册时间
     */
    private Date userRegisterTime;

}

