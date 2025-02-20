package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.util.Date;

/**
 * (TDAdmin)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:49
 */
@Data
@Schema(description = "管理员实体类")
public class TDAdmin {

    @TableId(type = IdType.AUTO)
    @Schema(name = "管理员ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long adminId;

    @Schema(name = "角色ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;

    @Schema(name = "管理员账户名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminName;

    @Schema(name = "管理员账号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminUsername;

    @Schema(name = "管理员密码", implementation = String.class,requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminPassword;

    @Schema(name = "启用状态", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminStatus;

    @Schema(name = "头像", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminAvatar;

    @Schema(name = "创建时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)

    private Date adminCreateTime;

}

