package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * (TDCompanyInfo)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "企业信息实体类")
public class TDCompanyInfo {

    @TableId(type = IdType.AUTO)
    @Schema(name = "企业ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyInfoId;

    @Schema(name = "邮箱", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoUsername;

    @Schema(name = "密码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoPassword;

    @Schema(name = "公司LOGO", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyLogo;

    @Schema(name = "账号注册时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date companyRegisterTime;

    @Schema(name = "企业名",implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoName;

    @Schema(name = "公司经营范围", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoScope;

    @Schema(name = "公司介绍", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoProfile;

    @Schema(name = "审核状态", description = "0代表待审核,1代表审核通过", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoReview;

    @Schema(name = "启用状态", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoStatus;

    @Schema(name = "公司资质文件路径", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String enterpriseFile;

    @Schema(name = "公司资质审核", description = "0代表待审核,1代表审核通过,2打回修改", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String enterpriseReview;

    @Schema(name = "公司所在地区", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInfoArea;

    @Schema(name = "公司规模", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String companySize;
}

