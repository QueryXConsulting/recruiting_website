package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.domain.TDRegistration;
import com.queryx.recruiting_website.domain.vo.RegistrationListVO;
import com.queryx.recruiting_website.domain.vo.ReservationListVO;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.mapper.TDRegistrationMapper;
import com.queryx.recruiting_website.service.MessageBoardService;
import com.queryx.recruiting_website.service.TDRegistrationService;

import com.queryx.recruiting_website.utils.PDFFormUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工入职信息服务实现类
 */
@Service
public class TDRegistrationServiceImpl extends ServiceImpl<TDRegistrationMapper, TDRegistration> implements TDRegistrationService {

    @Resource
    private MessageBoardService messageBoardService;
    @Resource
    private TDOffersMapper offersMapper;
    @Resource
    private TDJobResumeMapper jobResumeMapper;

    @Override
    public Object selectRegistration(Integer page, Integer size, Long jobId, String status) {
        Page<TDRegistration> registrationPage = page(
                new Page<>(page, size)
                , new LambdaQueryWrapper<TDRegistration>().eq(TDRegistration::getJobId, jobId)
                        .eq("2".equals(status), TDRegistration::getRegistrationStatus, status)
        );
        if (registrationPage.getRecords().isEmpty()) {
            return null;
        }
        Page<Object> registrationListVOPage = new Page<>(registrationPage.getCurrent(), registrationPage.getSize(), registrationPage.getTotal());
        registrationListVOPage.setRecords(
                registrationPage.getRecords().stream().map(registration -> {
                    if (Common.REGISTRATION_OK.equals(status)) {
                        ReservationListVO reservationListVO = new ReservationListVO();
                        BeanUtils.copyProperties(registration, reservationListVO);
                        return reservationListVO;
                    } else {
                        RegistrationListVO registrationListVO = new RegistrationListVO();
                        BeanUtils.copyProperties(registration, registrationListVO);
                        return registrationListVO;
                    }
                }).toList()
        );
        return registrationListVOPage;
    }

    @Override
    public Object updateRegistrationStatus(Long registrationId, String status, Date date) {
        LambdaUpdateWrapper<TDRegistration> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TDRegistration::getId, registrationId).set(StringUtils.hasText(status), TDRegistration::getRegistrationStatus, status);

        TDRegistration tdRegistration = getById(registrationId);
        TDOffers offers = offersMapper.selectById(tdRegistration.getOfferId());
        if (Common.REGISTRATION_OK.equals(status)) {
            messageBoardService.sendMessage(offers.getUserId(), "您的入职信息已通过 —此消息来自系统自动发送");
        }

        if (Common.REGISTRATION_REJECTED.equals(status)) {
            messageBoardService.sendMessage(offers.getUserId(), "您的入职信息未通过审核请重新提交 —此消息来自系统自动发送");
        }

        if (date != null) {
            updateWrapper.set(TDRegistration::getReservationStatus, "1")
                    .set(TDRegistration::getHireDate, date);
            messageBoardService.sendMessage(offers.getUserId(), "您的预约时间已发送,请进行确认 —此消息来自系统自动发送");
            LambdaUpdateWrapper<TDJobResume> resumeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            resumeLambdaUpdateWrapper
                    .eq(TDJobResume::getUserId, offers.getUserId())
                    .eq(TDJobResume::getJobId, offers.getJobId())
                    .set(TDJobResume::getResumeStatus, "6");
            jobResumeMapper.update(resumeLambdaUpdateWrapper);
        }

        update(updateWrapper);
        return null;
    }

    @Override
    public byte[] downloadPdf(Long id) throws IOException {
        String inputPath = "files/font/registration.pdf";
        TDRegistration registration = getById(id);
        Map<String, String> fieldValues = new HashMap<>();
        // 动态生成表单域值映射
        fieldValues.put("name", registration.getUserName()); // 姓名
        fieldValues.put("gender", registration.getGender()); // 性别
        fieldValues.put("birthDate", registration.getBirthDate().toString()); // 出生日期
        fieldValues.put("idCardNumber", registration.getIdCardNumber()); // 身份证号
        fieldValues.put("phoneNumber", registration.getPhoneNumber()); // 手机号码
        fieldValues.put("email", registration.getEmail()); // 邮箱地址
        fieldValues.put("hireDate", registration.getHireDate().toString()); // 入职日期
        fieldValues.put("position", registration.getPosition()); // 职位
        fieldValues.put("educationLevel", registration.getEducationLevel()); // 学历
        fieldValues.put("schoolName", registration.getSchoolName()); // 毕业学校
        fieldValues.put("bankAccount", registration.getBankAccount()); // 银行账号
        fieldValues.put("emergencyContact", registration.getEmergencyContact()); // 紧急联系人
        fieldValues.put("address", registration.getAddress()); // 家庭地址

        return PDFFormUtils.saveToByteArray(PDFFormUtils.fillPDFForm(inputPath, fieldValues, null, null));
    }


}

