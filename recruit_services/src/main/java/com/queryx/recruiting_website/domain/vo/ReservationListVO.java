package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationListVO {
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "主键，唯一标识员工", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer id;

    @Schema(name = "姓名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "性别", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @Schema(name = "手机号码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @Schema(name = "邮箱地址", implementation = String.class)
    private String email;

    @Schema(name = "入职日期", implementation = LocalDate.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate hireDate;

    @Schema(name = "最高学历", implementation = String.class)
    private String educationLevel;

    @Schema(name = "预约状态",description = "0待发送  1已发送 2已接受 3拒绝" ,implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String reservationStatus;


}
