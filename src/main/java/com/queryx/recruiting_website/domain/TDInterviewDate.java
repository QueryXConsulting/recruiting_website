package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDInterviewDate {




    @TableId
    private Long interviewDateId;

    private Long companyId;

    private Date interviewDate;

    private String isDeleted;

}

