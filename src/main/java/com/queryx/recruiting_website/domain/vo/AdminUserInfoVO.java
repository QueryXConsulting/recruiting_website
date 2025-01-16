package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminUserInfoVO {
   private List<String> permissions;
   private AdminInfoVO user;
}
