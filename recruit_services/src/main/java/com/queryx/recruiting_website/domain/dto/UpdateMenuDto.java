package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

@Data
public class UpdateMenuDto {

    private Long menuId;

    private String menuName;

    private Long parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String menuType;

    private String visible;

    private String status;

    private String perms;

    private String icon;

    private String remark;
}
