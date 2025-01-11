package com.queryx.recruiting_website.domain;

import java.util.Date;



import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * (TDResume)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@TableName("t_d_resume")
@Schema(name = "在线简历实体类")
public class TDResume {

    @TableId(value = "resume_id", type = IdType.AUTO)
    @Schema(name = "在线简历ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long resumeId;

    @TableField("resume_phone")
    @Schema(name = "手机号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumePhone;

    @Schema(name = "邮箱", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeEmail;

    @Schema(name = "性别", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeGender;

    @Schema(name = "生日", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date resumeBirth;

    @Schema(name = "婚姻情况", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeMarriage;

    @Schema(name = "政治面貌", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumePolitical;

    @Schema(name = "毕业院校", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeCollege;

    @Schema(name = "专业", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeMajor;

    @Schema(name = "教育经历", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeEducation;

    @Schema(name = "工作经验", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeExperience;

    @Schema(name = "意向工作", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeJob;

    @Schema(name = "意向薪资", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeSalary;

    @Schema(name = "个人介绍", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeIntroduction;

    @Schema(name = "审核状态", description = "0代表待审核,1代表审核通过", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeReview;

    @Schema(name = "启用状态", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)

    private String resumeStatus;

}

