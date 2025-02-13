package com.queryx.recruiting_website.domain.dto;


import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户登录数据传输对象
 */
@Data
@Schema(name = "用户登录DTO")
public class LoginDTO {

    @Schema(name = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String username;

    @Schema(name = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userPassword;

    @Schema(name = "用户角色", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userRole;

    @Schema(name = "用户验证码", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userCaptcha;
}
