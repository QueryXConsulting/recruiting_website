package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDResumeAttachments {


    @TableId
    private Long resumeAttachmentId;

    private Long userId;

    private String fileName;

    private Integer fileSize;

    private Date uploadDate;

    private String filePath;

    private String attachmentsReview;

    private String isDeleted;

}

