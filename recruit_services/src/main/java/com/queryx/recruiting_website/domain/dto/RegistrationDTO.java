package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author：fjj
 * @Date：2025/3/12 9:12
 */
@Data
public class RegistrationDTO {
    // 主键，唯一标识员工
    private Integer id;

    // 姓名
    private String userName;

    // 性别
    private String gender;

    // 出生日期
    private LocalDate birthDate;

    // 身份证号
    private String idCardNumber;

    // 手机号码
    private String phoneNumber;

    // 邮箱地址
    private String email;

    // 入职日期
    private LocalDate hireDate;

    // 职位
    private String position;

    // 入职状态，状态 0待提交 1待审核 2通过 3拒绝
//    private String registrationStatus;

    // 紧急联系人信息
    private String emergencyContact;

    // 银行账号
    private String bankAccount;

    // 最高学历
    private String educationLevel;

    // 家庭地址
    private String address;

    // 毕业学校名称
    private String schoolName;

    // 记录创建时间
//    private LocalDate createdAt;

    // offerId
//    private Long offerId;

    // jobId
//    private Long jobId;

    // 预约状态，预约状态0待选择  1已发送 2已接受 3拒绝
//    private String reservationStatus;

}
