package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleInfoDto {
    private Long roleId;

    private String roleName;

    private Integer roleSort;

    private String status;

    private List<Long> menuIds;
}
