package com.queryx.recruiting_website.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.util.Date;

/**
 * 在线简历DTO
 *
 * @author fjj
 * @since 2024-12-30 14:57:55
 */
@Data
@Schema(name = "在线简历DTO")
public class ResumeDTO {
    @Schema(name = "简历ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long resumeId;

    @Schema(name = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumePhone;

    @Schema(name = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeEmail;

    @Schema(name = "性别", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeGender;

    @Schema(name = "出生日期", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private Date resumeBirth;

    @Schema(name = "婚姻状况", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeMarriage;

    @Schema(name = "政治面貌", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumePolitical;

    @Schema(name = "毕业院校", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeCollege;

    @Schema(name = "专业", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeMajor;

    @Schema(name = "学历", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeEducation;

    @Schema(name = "最高学位", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeExperience;

    @Schema(name = "期望职位", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeJob;

    @Schema(name = "期望薪资", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeSalary;

    @Schema(name = "自我介绍", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeIntroduction;

}

