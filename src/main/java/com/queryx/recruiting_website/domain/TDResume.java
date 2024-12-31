package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDResume {

    @TableId
    private Long resumeId;

    private String resumePhone;

    private String resumeEmail;

    private String resumeGender;

    private Date resumeBirth;

    private String resumeMarriage;

    private String resumePolitical;

    private String resumeCollege;

    private String resumeMajor;

    private String resumeEducation;

    private String resumeExperience;

    private String resumeJob;

    private String resumeSalary;

    private String resumeIntroduction;

    private String resumeReview;

    private String resumeStatus;

}

