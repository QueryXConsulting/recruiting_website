package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 招聘信息VO类
 *
 * @author fjj
 * @since 2024-12-31 9:21:10
 */
@Data
public class JobVO {
    // 岗位id
    private Long jobId;
    // 关联企业
    private Long companyId;
    // 工作职位名称
    private String jobPosition;
    // 工作职位描述
    private String jobPositionDescribe;
    // 工种分类
    private String jobCategory;
    // 工作地点
    private String jobArea;
    // 人数
    private Integer jobPersonNumber;
    // 工资
    private String jobSalary;
    // 教育程度
    private String jobEducation;
    // 工作经验
    private String jobExperience;
    // 联系人名称
    private String jobContact;
    // 联系人手机号
    private String jobContactsPhone;
    // 岗位创建时间
    private Date jobTime;
    // 岗位浏览量
    private Integer jobView;
    // 工作性质
    private String jobNature;

}

