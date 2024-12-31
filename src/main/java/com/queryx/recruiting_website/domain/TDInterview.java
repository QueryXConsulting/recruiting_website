package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDInterview {




    @TableId
    private Long interviewId;

    private Long userId;

    private Long companyId;

    private String interviewJob;

    private String interviewResult;

    private Date interviewDate;

    private String interviewRegion;

    private String interviewType;

    private String interviewStatus;

    private String isDeleted;

}

