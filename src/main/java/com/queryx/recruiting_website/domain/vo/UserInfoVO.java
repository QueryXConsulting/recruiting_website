package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoVO {
    private String userName;
    private String userRole;
    private String userAvatar;
    private Integer userInterviews;
    private List<String> perms;
}
