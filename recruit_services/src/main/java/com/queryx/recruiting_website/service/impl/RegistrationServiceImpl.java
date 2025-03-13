package com.queryx.recruiting_website.service.impl;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDRegistration;
import com.queryx.recruiting_website.domain.dto.RegistrationDTO;
import com.queryx.recruiting_website.domain.vo.RegistrationStatusVO;
import com.queryx.recruiting_website.domain.vo.InfoInputVO;
import com.queryx.recruiting_website.domain.vo.ReservationRegistrationVO;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.mapper.TDRegistrationMapper;
import com.queryx.recruiting_website.service.RegistrationService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：fjj
 * @Date：2025/3/12 9:29
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private TDRegistrationMapper registrationMapper;
    @Autowired
    private TDOffersMapper offersMapper;
    @Autowired
    private TDJobResumeMapper jobResumeMapper;

    // 数据库字段由公司端创建
    private Integer registrationId;// 信息录入id
//    private Integer registrationId;// 信息录入id

    @Override
    public CommonResp<RegistrationStatusVO> queryRegistrationStatus() {
        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        // 查询信息录入状态
        TDRegistration status = registrationMapper.selectRegistrationStatus(userId);
        if (status == null || status.getId() == null || status.getRegistrationStatus() == null) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        RegistrationStatusVO statusVO = new RegistrationStatusVO();
        registrationId = status.getId();
        BeanUtils.copyProperties(status, statusVO);
        return CommonResp.success(statusVO);
    }

    @Override
    public CommonResp<InfoInputVO> queryInfoInputPosition() {
        if (registrationId == null) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        TDRegistration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        InfoInputVO infoInputVO = new InfoInputVO();
        infoInputVO.setPosition(registration.getPosition());
        return CommonResp.success(infoInputVO);
    }


    @Override
    public CommonResp<Boolean> insertRegistrationInfo(RegistrationDTO registrationDTO) {
        if (registrationId == null) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        // 保存信息录入信息
        TDRegistration registration = new TDRegistration();
        BeanUtils.copyProperties(registrationDTO, registration);
        registration.setId(registrationId);
        registration.setRegistrationStatus(Common.INPUT_STATUS_SEND);
        int updatedCount = registrationMapper.updateById(registration);
        if (updatedCount < 1) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
        }
        return CommonResp.success(true);
    }

    @Override
    public CommonResp<ReservationRegistrationVO> queryRegistrationInfo() {
        if (registrationId == null) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        ReservationRegistrationVO vo = registrationMapper.selectRegistrationInfo(registrationId);
        if (vo == null) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(vo);
    }


    @Override
    public CommonResp<Boolean> insertReservationStatus(String status) {
        if (registrationId == null) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        // 保存信息录入状态
        TDRegistration registration = new TDRegistration();
        registration.setId(registrationId);
        registration.setReservationStatus(status);
        int updatedCount = registrationMapper.updateById(registration);
        if (updatedCount < 1) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
        }
        return CommonResp.success(true);
    }

}
