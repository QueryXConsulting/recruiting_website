package com.queryx.recruiting_website.domain;

import java.util.Date;



import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * (TDResumeAttachments)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "附件简历实体类")
public class TDResumeAttachments {

    @TableId(value = "resume_attachment_id", type = IdType.AUTO)
    @Schema(name = "附件简历ID", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long resumeAttachmentId;

    @Schema(name = "用户ID", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "文件名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileName;

    @Schema(name = "文件大小", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fileSize;

    @Schema(name = "上传时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date uploadDate;

    @Schema(name = "文件路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String filePath;

    @Schema(name = "审核状态", description = "0代表待审核,1代表审核通过", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String attachmentsReview;

    @Schema(name = "删除状态", description = "0代表未删除，1代表已删除", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)

    private String isDeleted;

}

