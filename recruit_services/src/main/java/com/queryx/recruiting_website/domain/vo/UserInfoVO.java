package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoVO {
    private Long companyInfoId;
    private String userName;
    private String userRole;
    private String userAvatar;
    private Integer userInterviews;
    private String isFirstLogin;
    private String enterpriseReview;
    private String companyInfoReview;
    private List<String> permissions;
}
