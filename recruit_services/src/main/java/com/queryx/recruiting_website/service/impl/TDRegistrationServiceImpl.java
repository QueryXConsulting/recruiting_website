package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.domain.TDRegistration;
import com.queryx.recruiting_website.domain.vo.RegistrationListVO;
import com.queryx.recruiting_website.domain.vo.ReservationListVO;
import com.queryx.recruiting_website.mapper.TDRegistrationMapper;
import com.queryx.recruiting_website.service.TDRegistrationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 员工入职信息服务实现类
 */
@Service
public class TDRegistrationServiceImpl extends ServiceImpl<TDRegistrationMapper, TDRegistration> implements TDRegistrationService {

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
                    if ("2".equals(status)) {
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
        updateWrapper.eq(TDRegistration::getId, registrationId)
                .set(StringUtils.hasText(status), TDRegistration::getRegistrationStatus, status);

        if (date != null) {
            updateWrapper.set(TDRegistration::getReservationStatus, "1")
                    .set(TDRegistration::getHireDate,date);
        }

        update(updateWrapper);
        return null;
    }
}
