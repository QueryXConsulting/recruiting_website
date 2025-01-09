package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

@Data
public class UserVo {
    private Long userId;
    private String userName;
    private String userPhone;
    private String userRole;
    private String userStatus;
    private Integer userInterviews;
}
