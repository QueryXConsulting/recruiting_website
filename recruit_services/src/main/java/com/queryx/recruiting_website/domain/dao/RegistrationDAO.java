package com.queryx.recruiting_website.domain.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Author：fjj
 * @Date：2025/4/30 11:15
 */
@Data
public class RegistrationDAO {
    @Schema(name = "主键，唯一标识员工", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer id;

    @Schema(name = "入职状态",description = "状态 0待提交 1待审核 2通过 3拒绝" ,implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String registrationStatus;

    @Schema(name = "预约状态",description = "预约状态0待选择  1已发送 2已接受 3拒绝" ,implementation = String.class)
    private String reservationStatus;

    @Schema(name = "材料状态", implementation = String.class)
    private String status;
}
