package com.queryx.recruiting_website.domain;

import java.time.LocalDate;
import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class TDResume {



    /**
     * 在线简历id
     */
    @TableId(value = "resume_id", type = IdType.AUTO)
    private Long resumeId;
    /**
     * 手机号
     */
    @TableField("resume_phone")
    private String resumePhone;
    /**
     * 邮箱
     */
    private String resumeEmail;
    /**
     * 性别
     */
    private String resumeGender;
    /**
     * 出生日期
     */
    private Date resumeBirth;
    /**
     * 婚姻状况
     */
    private String resumeMarriage;
    /**
     * 政治背景
     */
    private String resumePolitical;
    /**
     * 毕业院校
     */
    private String resumeCollege;
    /**
     * 专业
     */
    private String resumeMajor;
    /**
     * 教育程度
     */
    private String resumeEducation;
    /**
     * 工作经验
     */
    private String resumeExperience;
    /**
     * 意向工作
     */
    private String resumeJob;
    /**
     * 意向工资
     */
    private String resumeSalary;
    /**
     * 个人介绍
     */
    private String resumeIntroduction;
    /**
     * 0代表待审核,1代表审核通过
     */
    private String resumeReview;
    /**
     * 0代表启用,1代表禁用
     */
    private String resumeStatus;

}

