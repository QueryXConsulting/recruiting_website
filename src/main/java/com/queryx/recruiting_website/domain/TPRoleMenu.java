package com.queryx.recruiting_website.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TPRoleMenu {

    @TableId
    private Long roleMenuId;

    private Long menuId;

    private Long roleId;

}

