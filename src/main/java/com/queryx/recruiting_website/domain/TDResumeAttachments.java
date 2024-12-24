package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * (TDResumeAttachments)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDResumeAttachments {



    /**
     * 附件简历id
     */
    private Long resumeAttachmentId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件大小
     */
    private Integer fileSize;
    /**
     * 上传日期
     */
    private Date uploadDate;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 0代表待审核,1代表审核通过
     */
    private String attachmentsReview;
    /**
     * 0代表未删除，1代表已删除
     */
    private String isDeleted;

}

