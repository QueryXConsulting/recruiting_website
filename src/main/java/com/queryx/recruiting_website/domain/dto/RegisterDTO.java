package com.queryx.recruiting_website.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 用户注册数据传输对象
 */
@Data
@ToString
public class RegisterDTO {
    // 用户真实姓名
    private String userName;
    // 用户密码
    private String userPassword;
    // 用户角色
    private String userRole;
    // 用户头像
    private String userAvatar;
    // 用户手机号
    private String resumePhone;
    // 用户邮箱
    private String resumeEmail;
    // 用户性别
    private String resumeGender;
    // 用户生日
    private Date resumeBirth;
    // 婚姻状况
    private String resumeMarriage;
    // 政治面貌
    private String resumePolitical;
    // 毕业院校
    private String resumeCollege;
    // 专业
    private String resumeMajor;
    // 学历
    private String resumeEducation;
    // 工作经验
    private String resumeExperience;
    // 意向工作
    private String resumeJob;
    // 期望薪资
    private String resumeSalary;
    // 个人介绍
    private String resumeIntroduction;



}
