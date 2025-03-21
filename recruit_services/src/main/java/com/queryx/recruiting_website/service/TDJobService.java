package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.dto.JobDto;
import com.queryx.recruiting_website.domain.vo.JobCompanyListVO;
import com.queryx.recruiting_website.domain.dto.JobDetailDto;
import com.queryx.recruiting_website.domain.dto.JobInsertDto;


public interface TDJobService extends IService<TDJob> {


    IPage<JobCompanyListVO> selectJobList(Integer page, Integer size, String companyName, String jobName
            , String jobReview, String status, String jobCategory, String jobNature);

    JobDetailDto selectJobInfo(Long jobId);

    JobDetailDto updateJob(JobDetailDto jobDetailDto);

    JobDetailDto insertJobInfo(JobInsertDto jobInsertDto);


    Object deleteJob(Long jobId);


    Object addJob(JobDto jobDto);

    Object jobReview(String review, Long jobId);

    Object selectCompanyJobList(Integer page, Integer size, String jobName, String jobReview, String jobCategory);
}

