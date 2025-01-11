package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * (TPRole)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "角色实体类")
public class TPRole {

    @TableId(type = IdType.AUTO)
    @Schema(name = "角色ID", implementation = Long.class, requiredMode = Schema.RequiredMode.AUTO)
    private Long roleId;

    @Schema(name = "角色名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @Schema(name = "显示顺序", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer roleSort;

    @Schema(name = "启用状态", description = "0代表正常，1代表停用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(name = "删除状态", description = "0代表未删除,1代表已删除", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String delFlag;

    @Schema(name = "创建时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createTime;

    @Schema(name = "更新者", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long updateBy;

    @Schema(name = "更新时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)

    private Date updateTime;

}

