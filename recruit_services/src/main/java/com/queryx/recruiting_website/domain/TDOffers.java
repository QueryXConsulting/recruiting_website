package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


@Data
@Schema(description = "offers类")
public class TDOffers {

    @TableId(type = IdType.AUTO)
    @Schema(name = "offerID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long offersId;

    @Schema(name = "用户ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "工作ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;


    @Schema(name = "offers状态", description = "状态0待发送 1是已接受 2拒绝 3撤销", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String offersStatus;

    @Schema(name = "发送时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date offersDate;

    @Schema(name = "offer文件存储路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String offersFilePath;

}

