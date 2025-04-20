package com.queryx.recruiting_website.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 用户注册数据传输对象
 */
@Data
@Schema(name = "用户注册DTO")
public class RegisterDTO {

    @Schema(name = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userName;

    @Schema(name = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userPassword;

    @Schema(name = "用户手机号", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userPhone;

    @Schema(name = "用户邮箱", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userEmail;


}
