package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * (TDUser)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "用户信息实体类")
public class TDUser {

    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(name = "用户id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "邮箱", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @Schema(name = "在线简历ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long resumeId;

    @Schema(name = "用户名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "手机号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userPhone;

    @Schema(name = "密码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userPassword;

    @Schema(name = "用户角色", description = "0代表学生用户,1代表公司用户", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userRole;

    @Schema(name = "启用状态", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userStatus;

    @Schema(name = "头像", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userAvatar;

    @Schema(name = "待面试数量", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer userInterviews;

    @Schema(name = "注册时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date userRegisterTime;

    @Schema(name = "删除标志", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String delFlag;

}

