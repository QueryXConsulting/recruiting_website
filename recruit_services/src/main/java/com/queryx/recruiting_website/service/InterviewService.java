package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDInterview;
import com.queryx.recruiting_website.domain.dto.SendInterviewDto;
import com.queryx.recruiting_website.domain.dto.UpdateInterviewDto;
import com.queryx.recruiting_website.domain.vo.InterviewDateVO;
import com.queryx.recruiting_website.domain.vo.InterviewVO;
import com.queryx.recruiting_website.utils.CommonResp;

import java.util.Date;
import java.util.List;


public interface InterviewService extends IService<TDInterview> {


    Object sendInterviewDto(SendInterviewDto sendInterviewDto);

    Object selectInterviewList(Integer page, Integer size, Long jobId);

    Object updateInterview(UpdateInterviewDto updateInterviewDto);

    Object offerList(Integer page, Integer size);

    /**
     * 查询用户所有面试信息
     *
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 面试信息列表
     */
    CommonResp<Page<InterviewVO>> getInterviews(Long userId, Integer page, Integer pageSize);

    /**
     * 查询面试信息
     *
     * @param id 面试id
     * @return 面试信息
     */
    CommonResp<InterviewVO> getInterview(Long id);

    /**
     * 查询面试时间段
     *
     * @param id 公司id
     * @return 面试时间段
     */
    CommonResp<List<InterviewDateVO>> getInterviewDate(Long id);

    /**
     * 接受面试
     *
     * @param interviewId 面试id
     * @param isAccept 是否接受
     * @param interviewDate 面试时间
     * @return 是否成功
     */
    CommonResp<Boolean> isAcceptInterview(Long interviewId, String isAccept, Date interviewDate);
}
