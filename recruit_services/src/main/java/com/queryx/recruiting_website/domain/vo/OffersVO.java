package com.queryx.recruiting_website.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Author：fjj
 * @Date：2025/3/3 15:47
 */
@Data
@Schema(description = "offers类")
public class OffersVO {

    // 主键ID
    @TableId(value = "offers_id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "offers表id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long offerId;

    // 用户ID
    @Schema(name = "用户id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    // 职位ID
    @Schema(name = "职位id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(name = "职位名称", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String jobPosition;

    @Schema(name = "公司名", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String companyInfoName;

    // offer状态
    @Schema(name = "offer状态", description = "offer状态：0-接受，1-拒绝，2-未选择", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String offersStatus;

    // offer发放时间
    @Schema(name = "offer发放时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date offersDate;

    // offer存储路径
    @Schema(name = "offer存储路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String offersFilePath;
}
