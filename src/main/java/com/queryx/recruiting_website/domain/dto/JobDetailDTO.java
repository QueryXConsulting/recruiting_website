package com.queryx.recruiting_website.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class JobDetailDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    private String companyName;

    @JsonSerialize(using = ToStringSerializer.class)
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

    private String jobStatus;
}
