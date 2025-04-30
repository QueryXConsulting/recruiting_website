package com.queryx.recruiting_website.service.impl;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDRegistration;
import com.queryx.recruiting_website.domain.dao.RegistrationDAO;
import com.queryx.recruiting_website.domain.dto.RegistrationDTO;
import com.queryx.recruiting_website.domain.vo.RegistrationStatusVO;
import com.queryx.recruiting_website.domain.vo.InfoInputVO;
import com.queryx.recruiting_website.domain.vo.ReservationRegistrationVO;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.mapper.TDRegistrationMapper;
import com.queryx.recruiting_website.service.MessageBoardService;
import com.queryx.recruiting_website.service.RegistrationService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：fjj
 * @Date：2025/3/12 9:29
 */
@Slf4j
@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private TDRegistrationMapper registrationMapper;
    @Autowired
    private TDOffersMapper offersMapper;
    @Autowired
    private TDJobResumeMapper jobResumeMapper;
    @Autowired
    private TDJobMapper jobMapper;
    @Autowired
    private MessageBoardService messageBoardService;
    private final String INPUT_INFO_MESSAGE_CONTENT = "我已经录入完成了您发布的%s岗位的入职信息，请及时处理。 ——此消息由系统自动发送，请勿回复。";
    private final String RESERVATION_MESSAGE_CONTENT = "我已经预约完成了您发布的%s岗位的报到时间，请注意查收。 ——此消息由系统自动发送，请勿回复。";

    // 数据库字段由公司端创建
    private Integer registrationId;// 信息录入id

    @Override
    public CommonResp<RegistrationStatusVO> queryRegistrationStatus() {
        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        // 查询信息录入状态
        RegistrationDAO status = registrationMapper.selectRegistrationStatus(userId);
        if (status == null || status.getId() == null || status.getStatus() == null) {
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
//        registration.setRegistrationStatus(Common.INPUT_STATUS_SEND);
        int updatedCount = registrationMapper.updateById(registration);
        if (updatedCount < 1) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
        }
        Long jobId = registrationMapper.selectById(registrationId).getJobId();
        TDJob job = jobMapper.selectById(jobId);
        Boolean b = messageBoardService.saveMessage(job.getCompanyId(), String.format(INPUT_INFO_MESSAGE_CONTENT, job.getJobPosition()));
        if (!b) {
            log.warn("消息发送失败,消息录入id:{}, 岗位id:{}", registrationId, jobId);
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
        Long jobId = registrationMapper.selectById(registrationId).getJobId();
        TDJob job = jobMapper.selectById(jobId);
        Boolean b = messageBoardService.saveMessage(job.getCompanyId(), String.format(RESERVATION_MESSAGE_CONTENT, job.getJobPosition()));
        if (!b) {
            log.warn("消息发送失败,预约报到id:{}, 岗位id:{}", registrationId, jobId);
        }
        return CommonResp.success(true);
    }

}
