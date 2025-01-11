package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

@Data
public class AdminVo {

    private Long adminId;

    private String roleName;

    private String adminName;

    private String adminUsername;

    private String adminStatus;
}
