package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private Long companyId;
    private String token;
}
