package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;


import java.util.Date;

@Data
public class AttachmentsResumeVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resumeAttachmentId;
    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
}
