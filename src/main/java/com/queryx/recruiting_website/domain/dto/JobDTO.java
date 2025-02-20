package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

@Data
public class JobDTO {

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


    private String jobNature;
}
