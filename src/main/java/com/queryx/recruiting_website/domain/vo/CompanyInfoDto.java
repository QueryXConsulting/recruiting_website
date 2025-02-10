package com.queryx.recruiting_website.domain.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CompanyInfoDto {

    @Schema(name = "企业ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyInfoId;

    @Schema(name = "企业用户名称", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "账号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoUsername;

    @Schema(name = "密码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoPassword;

    @Schema(name = "公司LOGO", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyLogo;

    @Schema(name = "账号注册时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date companyRegisterTime;

    @Schema(name = "企业名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoName;

    @Schema(name = "公司经营范围", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoScope;

    @Schema(name = "公司介绍", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoProfile;

    @Schema(name = "审核状态", description = "0代表待审核,1代表审核通过,2代表打回修改", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoReview;

    @Schema(name = "启用状态", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String CompanyInfoStatus;

}
