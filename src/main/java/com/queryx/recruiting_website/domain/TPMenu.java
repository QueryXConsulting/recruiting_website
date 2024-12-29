package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TPMenu)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TPMenu {



    /**
     * 菜单id
     */
    @TableId
    private Long menuId;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 父菜单id
     */
    private Long parentId;
    /**
     * 显示顺序
     */
    private Integer orderNum;
    /**
     * 路由地址
     */
    private String path;
    /**
     * 组件路径
     */
    private String component;
    /**
     * 菜单类型(m目录 c菜单 f按钮)
     */
    private String menuType;
    /**
     * 菜单状态(0显示 1隐藏)
     */
    private String visible;
    /**
     * 菜单状态(0正常 1停用)
     */
    private String status;
    /**
     * 权限标识
     */
    private String perms;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 创建者
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新者
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 删除标志，0表示未删除，1表示已删除
     */
    private String delFlag;

}

