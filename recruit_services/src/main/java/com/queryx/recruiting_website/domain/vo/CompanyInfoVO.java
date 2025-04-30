package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * (TDCompanyInfo)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "企业信息实体类")
public class CompanyInfoVO {


    @Schema(name = "企业ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyInfoId;

    @Schema(name = "企业用户名称", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "账号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoUsername;

    @Schema(name = "公司LOGO", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyLogo;

    @Schema(name = "公司资质文件", description = "文件路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> enterpriseFiles;

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
    private String companyInfoStatus;

    @Schema(name = "公司资质审核", description = "0代表待审核,1代表审核通过,2打回修改", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String enterpriseReview;

    @Schema(name = "公司规模", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companySize;

}

