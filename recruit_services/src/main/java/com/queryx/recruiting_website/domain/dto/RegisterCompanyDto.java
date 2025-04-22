package com.queryx.recruiting_website.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterCompanyDto {
    @Schema(name = "公司所在地", description = "0代表待审核,1代表审核通过,2打回修改", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoArea;

    @Schema(name = "邮箱", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoUsername;

    @Schema(name = "密码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoPassword;
    @Schema(name = "确认密码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;

    @Schema(name = "企业名",implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoName;

    @Schema(name = "用户名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "手机号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userPhone;

    @Schema(name = "公司规模", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companySize;
}
