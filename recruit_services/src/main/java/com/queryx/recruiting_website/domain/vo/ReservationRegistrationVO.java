package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author：fjj
 * @Date：2025/3/13 10:45
 */
@Data
public class ReservationRegistrationVO {
    // 职位
    private String position;
    // 姓名
    private String userName;
    // 入职日期
    private LocalDate hireDate;
    // 公司名
    private String companyInfoName;
    // 工作地点
    private String jobArea;
}
