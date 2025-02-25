package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 所有简历（包括在线简历和附件简历）
 *
 * @author fjj
 * @date 2025-02-25
 */
@Data
public class AllResumeVO {
    // 原属性
//    private ResumeVO resumeVO;
//    private List<AttachmentsResumeVO> attachmentsResumeVO;

    // 修改属性
    private Long resumeId;// 简历ID
    private Date userRegisterTime;// 用户注册时间
    List<AttachmentsResume> attachmentsResumes;// 附件简历列表

    @Data
    public static class AttachmentsResume {
        private Long resumeAttachmentId;// 附件ID
        private String fileName;// 文件名
        private Integer fileSize;// 文件大小
        private Date uploadDate;// 上传时间
        private String filePath;// 文件路径
    }
}
