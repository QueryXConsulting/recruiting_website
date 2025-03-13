package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.domain.dto.RegistrationDTO;
import com.queryx.recruiting_website.domain.vo.RegistrationStatusVO;
import com.queryx.recruiting_website.domain.vo.InfoInputVO;
import com.queryx.recruiting_website.domain.vo.ReservationRegistrationVO;
import com.queryx.recruiting_website.utils.CommonResp;

/**
 * @Author：fjj
 * @Date：2025/3/12 9:10
 */
public interface RegistrationService {

    /**
     * 查询信息录入的状态
     * @return 状态码
     */
    CommonResp<RegistrationStatusVO > queryRegistrationStatus();

    /**
     * 查询信息录入的职位
     * @return 职位列表
     */
    CommonResp<InfoInputVO> queryInfoInputPosition();

    /**
     * 插入注册信息
     * @param registrationDTO 录入的信息
     * @return 成功与否
     */
    CommonResp<Boolean> insertRegistrationInfo(RegistrationDTO registrationDTO);

    /**
     * 查询用户报到确认信息
     * @return 用户报到确认信息
     */
    CommonResp<ReservationRegistrationVO> queryRegistrationInfo();

    /**
     * 更新用户报到状态
     * @param status 用户报到状态
     * @return 成功与否
     */
    CommonResp<Boolean> insertReservationStatus(String status);
}
