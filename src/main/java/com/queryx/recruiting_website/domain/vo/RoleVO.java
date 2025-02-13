package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleVO {
    private Long roleId;

    private String roleName;

    private Integer roleSort;

    private List<String> menuName;

}
