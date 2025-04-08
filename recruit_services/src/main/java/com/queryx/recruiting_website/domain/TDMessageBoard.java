package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "留言板表实体类")
public class TDMessageBoard {

    @TableId(type = IdType.AUTO)
    @Schema(name = "messageId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long messageId;

    @Schema(name = "userId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "companyId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @Schema(name = "content", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(name = "createTime", implementation = LocalDateTime.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(name = "isRead", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String isRead;

    @Schema(name = "应聘者是否已读(0未读,1已读)", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String applicantReadStatus;

    @Schema(name = "删除标志", description = "0:未删除,1:已删除", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer isDeleted;

    @Schema(name = "留言所属者", description = "0:公司,1:应聘者", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String ownerUser;
}
