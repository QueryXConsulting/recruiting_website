package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDInterview;
import com.queryx.recruiting_website.domain.dto.SendInterviewDto;
import com.queryx.recruiting_website.domain.dto.UpdateInterviewDto;


public interface InterviewService extends IService<TDInterview> {


    Object sendInterviewDto(SendInterviewDto sendInterviewDto);

    Object selectInterviewList(Integer page, Integer size, Long jobId);

    Object updateInterview(UpdateInterviewDto updateInterviewDto);

    Object offerList(Integer page, Integer size);
}

