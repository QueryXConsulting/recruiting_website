package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminUserInfoVo {
   private List<String> permissions;
   private AdminInfoVo user;
}
