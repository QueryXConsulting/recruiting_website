package com.queryx.recruiting_website.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;


@Data
public class ResumeVO {

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

}

