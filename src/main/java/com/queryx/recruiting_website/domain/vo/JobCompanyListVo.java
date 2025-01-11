package com.queryx.recruiting_website.domain.vo;
import lombok.Data;
import java.util.Date;


@Data
public class JobCompanyListVo {

    private Long jobId;

    private String companyName;

    private String jobPosition;

    private String jobEducation;

    private String jobExperience;

    private Date jobTime;

    private String jobNature;

    private String jobReview;

    private String jobStatus;

}
