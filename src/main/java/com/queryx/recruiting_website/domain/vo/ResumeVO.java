package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ResumeVO {
    private Long resumeId;
    /**
     * 手机号
     */
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
}
