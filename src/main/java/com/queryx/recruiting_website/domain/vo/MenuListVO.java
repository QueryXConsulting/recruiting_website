package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MenuListVO {
    private String component;
    private String remark;
    private String icon;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;
    private String menuName;
    private String menuType;
    private Integer orderNum;
    private Date createTime;
    private Long parentId;
    private String path;
    private String perms;
    private String status;
    private String visible;
    private List<MenuListVO> children;
}
