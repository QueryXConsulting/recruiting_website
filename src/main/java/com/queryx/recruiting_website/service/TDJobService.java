package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.vo.JobCompanyListVo;
import com.queryx.recruiting_website.domain.dto.JobDetailDto;
import com.queryx.recruiting_website.domain.dto.JobInsertDto;



public interface TDJobService extends IService<TDJob> {


    IPage<JobCompanyListVo> selectJobList(Integer page,Integer size,Long companyId);

    JobDetailDto selectJobInfo(Long jobId);

    JobDetailDto updateJob(JobDetailDto jobDetailDto);

    JobDetailDto insertJobInfo(JobInsertDto jobInsertDto, Long companyId);


    Object deleteJob(Long jobId);
}

