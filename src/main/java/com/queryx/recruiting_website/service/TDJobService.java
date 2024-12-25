package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.vo.JobCompanyListVo;
import com.queryx.recruiting_website.vo.JobDetailVo;
import com.queryx.recruiting_website.vo.JobInsertVo;


/**
 * (TDJob)表服务接口
 *
 * @author makejava
 * @since 2024-12-23 13:10:59
 */
public interface TDJobService extends IService<TDJob> {


    IPage<JobCompanyListVo> selectJobList(Integer page,Integer size,Long companyId);

    JobDetailVo selectJobInfo(Long jobId);

    JobDetailVo updateJob(JobDetailVo jobDetailVo);

    JobDetailVo insertJobInfo(JobInsertVo jobInsertVo, Long companyId);


}

