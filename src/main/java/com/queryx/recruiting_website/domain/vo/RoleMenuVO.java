package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuVO {
    private Long menuId;
    private String menuName;
    List<RoleMenuVO> children;
}
