package com.queryx.recruiting_website.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class UserCompanyVO {

    @Schema(name = "用户id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "邮箱", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @Schema(name = "用户名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "手机号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userPhone;

    @Schema(name = "密码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userPassword;

    @Schema(name = "头像", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userAvatar;

    @Schema(name = "注册时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date userRegisterTime;

}
