package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleInfoDTO {
    private Long roleId;

    private String roleName;

    private Integer roleSort;

    private String status;

    private String roleType;

    private List<Long> menuIds;
}
