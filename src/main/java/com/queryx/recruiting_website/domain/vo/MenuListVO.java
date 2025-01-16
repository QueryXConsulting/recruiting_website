package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

@Data
public class MenuListVO {
    private String component;
    private String remark;
    private String icon;
    private Long menuId;
    private String menuName;
    private String menuType;
    private Integer orderNum;
    private Long parentId;
    private String path;
    private String perms;
    private String status;
    private String visible;
}
