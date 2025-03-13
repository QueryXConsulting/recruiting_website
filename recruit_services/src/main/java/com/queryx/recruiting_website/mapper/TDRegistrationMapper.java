package com.queryx.recruiting_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.queryx.recruiting_website.domain.TDRegistration;
import com.queryx.recruiting_website.domain.dto.RegistrationDTO;
import com.queryx.recruiting_website.domain.vo.ReservationRegistrationVO;

/**
 * 员工入职信息 Mapper 接口
 */
public interface TDRegistrationMapper extends BaseMapper<TDRegistration> {


    /**
     * 查询状态
     *
     * @param userId 用户id
     * @return 员工入职信息
     */
    TDRegistration selectRegistrationStatus(Long userId);


    /**
     * 查询用于用户报到确认的信息
     *
     * @param id registrationId
     * @return 用于用户报到确认的信息
     */
    ReservationRegistrationVO selectRegistrationInfo(Integer id);


}
