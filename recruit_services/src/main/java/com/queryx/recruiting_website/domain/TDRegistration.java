package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;


@Data
public class TDRegistration {

    @TableId
    @Schema(name = "主键，唯一标识员工", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer id;

    @Schema(name = "姓名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "性别", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @Schema(name = "出生日期", implementation = LocalDate.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate birthDate;

    @Schema(name = "身份证号", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String idCardNumber;

    @Schema(name = "手机号码", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @Schema(name = "邮箱地址", implementation = String.class)
    private String email;

    @Schema(name = "入职日期", implementation = LocalDate.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate hireDate;

    @Schema(name = "职位", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String position;

    @Schema(name = "入职状态",description = "状态 0待提交 1待审核 2通过 3拒绝" ,implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String registrationStatus;

    @Schema(name = "紧急联系人信息", implementation = String.class)
    private String emergencyContact;

    @Schema(name = "银行账号", implementation = String.class)
    private String bankAccount;

    @Schema(name = "最高学历", implementation = String.class)
    private String educationLevel;

    @Schema(name = "家庭地址", implementation = String.class)
    private String address;

    @Schema(name = "毕业学校名称", implementation = String.class)
    private String schoolName;

    @Schema(name = "记录创建时间", implementation = LocalDate.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate createdAt;

    @Schema(name = "offerId", implementation = Long.class)
    private Long offerId;

    @Schema(name = "jobId", implementation = Long.class)
    private Long jobId;

    @Schema(name = "预约状态",description = "预约状态0待选择  1已发送 2已接受 3拒绝" ,implementation = String.class)
    private String reservationStatus;
}
