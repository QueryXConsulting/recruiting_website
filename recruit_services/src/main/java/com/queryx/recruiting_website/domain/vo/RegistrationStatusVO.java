package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

/**
 * @Author：fjj
 * @Date：2025/3/13 10:00
 */
@Data
public class RegistrationStatusVO {
    // 信息录入状态
//    private String registrationStatus;
    // 材料审核状态
    private String status;
    // 预约状态
    private String reservationStatus;
}
