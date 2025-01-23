package com.queryx.recruiting_website.domain;



import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * (TPRoleMenu)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "角色菜单关系表")
public class TPRoleMenu {

    @TableId(type = IdType.AUTO)
    @Schema(name = "ID", implementation = String.class, requiredMode = Schema.RequiredMode.AUTO)
    private Long roleMenuId;

    @Schema(name = "菜单ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long menuId;

    @Schema(name = "角色ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;

}

