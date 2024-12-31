package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDJob {


    @TableId
    private Long jobId;

    private Long companyId;

    private String jobPosition;

    private String jobPositionDescribe;

    private String jobCategory;

    private String jobArea;

    private Integer jobPersonNumber;

    private String jobSalary;

    private String jobEducation;

    private String jobExperience;

    private String jobContact;

    private String jobContactsPhone;

    private Date jobTime;

    private Integer jobView;

    private String jobNature;

    private String jobReview;

    private String jobStatus;

}

