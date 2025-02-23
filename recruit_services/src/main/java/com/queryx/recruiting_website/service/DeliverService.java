package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.domain.dto.DeliverResumeDTO;

public interface DeliverService {

    /**
     * 插入简历投递信息
     *
     * @param deliverResumeDTO 简历投递信息
     * @return 插入的行数
     */
    int insertDeliverResume(DeliverResumeDTO deliverResumeDTO);
}
