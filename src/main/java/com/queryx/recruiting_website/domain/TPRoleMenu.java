package com.queryx.recruiting_website.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * (TPRoleMenu)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TPRoleMenu {

    /**
     * 关联表id
     */
    private Long roleMenuId;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 角色ID
     */
    private Long roleId;

}

