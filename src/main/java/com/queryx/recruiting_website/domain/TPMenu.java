package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.queryx.recruiting_website.domain.vo.MenuVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * (TPMenu)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "菜单实体类")
public class TPMenu {


    @TableId(type = IdType.AUTO)
    @Schema(name = "菜单ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long menuId;

    @Schema(name = "菜单名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuName;

    @Schema(name = "父菜单ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    @Schema(name = "显示顺序", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderNum;

    @Schema(name = "路由地址", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;

    @Schema(name = "组件路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String component;

    @Schema(name = "菜单类型", description = "m目录 c菜单 f按钮", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuType;

    @Schema(name = "菜单状态", description = "0显示 1隐藏", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String visible;

    @Schema(name = "菜单状态", description = "0正常 1停用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(name = "权限标识", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String perms;

    @Schema(name = "菜单图标", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String icon;

    @Schema(name = "创建者", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long createBy;

    @Schema(name = "创建时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createTime;

    @Schema(name = "更新者", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long updateBy;

    @Schema(name = "更新时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date updateTime;

    @Schema(name = "备注", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;

    @Schema(name = "删除标志", description = "0表示未删除，1表示已删除", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String delFlag;

    @TableField(exist = false)
    @Schema(name = "菜单子树", description = "不存在的字段,用于getRouter接口下存储子树", implementation = List.class)
    private List<MenuVO> children;

}

