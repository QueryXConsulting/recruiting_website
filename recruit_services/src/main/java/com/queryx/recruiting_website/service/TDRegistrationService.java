package com.queryx.recruiting_website.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDRegistration;

import java.io.IOException;
import java.util.Date;


/**
 * 员工入职信息服务接口
 */
public interface TDRegistrationService extends IService<TDRegistration> {

    Object selectRegistration(Integer page, Integer size, Long jobId, String status);

    Object updateRegistrationStatus(Long registrationId, String status, Date date);

    byte[] downloadPdf(Long id) throws IOException;
}
