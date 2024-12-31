package com.queryx.recruiting_website.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDJobResume {


    @TableId
    private Long jobResumeId;


    private Long jobId;


    private Long resumeId;


    private String resumeName;


    private String resumeType;

}

