package com.queryx.recruiting_website.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

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

    @Schema(name = "用户角色", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userRole;

    @Schema(name = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String userAvatar;

    @Schema(name = "用户手机号", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumePhone;

    @Schema(name = "用户邮箱", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeEmail;

    @Schema(name = "用户性别", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeGender;

    @Schema(name = "用户生日", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Date.class)
    private Date resumeBirth;

    @Schema(name = "用户婚姻状况", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeMarriage;

    @Schema(name = "政治面貌", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumePolitical;

    @Schema(name = "毕业院校", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeCollege;

    @Schema(name = "专业", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeMajor;

    @Schema(name = "学历", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeEducation;

    @Schema(name = "工作经验", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeExperience;

    @Schema(name = "工作职位", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeJob;

    @Schema(name = "专业技能", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeSkills;

    @Schema(name = "期望薪资", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeSalary;

    @Schema(name = "自我介绍", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeIntroduction;


}
