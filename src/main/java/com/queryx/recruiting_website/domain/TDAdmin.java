package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TDAdmin)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:49
 */
@Data
public class TDAdmin {



    /**
     * 管理员id
     */
    @TableId
    private Long adminId;
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 管理员账户名称
     */
    private String adminName;
    /**
     * 管理员账号
     */
    private String adminUsername;
    /**
     * 管理员密码
     */
    private String adminPassword;
    /**
     * 0代表启用,1代表禁用
     */
    private String adminStatus;
    /**
     * 头像
     */
    private String adminAvatar;
    /**
     * 创建时间
     */
    private Date adminCreateTime;

}

