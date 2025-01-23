package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;


@Data
public class JobCompanyListVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long jobId;

    private String companyName;

    private String jobSalary;


    private String jobCategory;

    private String jobPosition;

    private String jobContact;

    private String jobEducation;

    private String jobExperience;

    private Date jobTime;

    private String jobNature;

    private String jobReview;

    private String jobStatus;

    private String jobPositionDescribe;

    private String jobArea;

}
