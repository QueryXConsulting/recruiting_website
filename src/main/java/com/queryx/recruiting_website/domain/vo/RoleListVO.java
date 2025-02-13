package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RoleListVO {
    private Long roleId;
    private String roleName;
    private String status;
    private Date createTime;
}
