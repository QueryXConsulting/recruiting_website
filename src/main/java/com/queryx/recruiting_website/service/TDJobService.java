package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.dto.JobDTO;
import com.queryx.recruiting_website.domain.vo.JobCompanyListVO;
import com.queryx.recruiting_website.domain.dto.JobDetailDTO;
import com.queryx.recruiting_website.domain.dto.JobInsertDTO;


public interface TDJobService extends IService<TDJob> {


    IPage<JobCompanyListVO> selectJobList(Integer page, Integer size, Long companyId,String jobName,String jobReview,String status);

    JobDetailDTO selectJobInfo(Long jobId);

    JobDetailDTO updateJob(JobDetailDTO jobDetailDTO);

    JobDetailDTO insertJobInfo(JobInsertDTO jobInsertDTO, Long companyId);


    Object deleteJob(Long jobId);


    Object addJob(JobDTO jobDTO);

    Object jobReview(String review, Long jobId);
}

