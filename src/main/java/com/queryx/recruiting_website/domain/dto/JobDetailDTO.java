package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class JobDetailDTO {


    private Long companyId;

    private String companyName;

    private Long jobId;

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

    private String jobNature;

    private String jobReview;

}
