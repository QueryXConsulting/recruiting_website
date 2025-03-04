package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.domain.dto.DeliverResumeDTO;
import com.queryx.recruiting_website.domain.vo.JobResumeVO;
import com.queryx.recruiting_website.utils.CommonResp;

public interface DeliverService {

    /**
     * 插入简历投递信息
     *
     * @param deliverResumeDTO 简历投递信息
     * @return 插入的行数
     */
    int insertDeliverResume(DeliverResumeDTO deliverResumeDTO);

    /**
     * 根据用户ID查询简历投递列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 简历投递列表
     */
    CommonResp<Page<JobResumeVO>> queryJobResumeList(Long userId, Integer pageNum, Integer pageSize);
}
