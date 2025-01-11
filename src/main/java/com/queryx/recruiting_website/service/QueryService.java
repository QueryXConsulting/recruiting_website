package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.domain.vo.AttachmentsResumeVO;
import com.queryx.recruiting_website.domain.vo.InterviewVO;
import com.queryx.recruiting_website.domain.vo.JobVO;
import com.queryx.recruiting_website.domain.vo.ResumeVO;

import java.util.List;

public interface QueryService {

    /**
     * 查询在线简历信息
     * @param id 简历id
     * @return 简历信息
     */
    ResumeVO getOnlineResume(Long id);
    /**
     * 查询在线简历信息
     * @param id 用户id
     * @return 简历信息
     */
    List<AttachmentsResumeVO> getResumeAttachmentList(Long id);

    /**
     * 查询面试信息
     * @param id 用户id
     * @return 面试信息
     */
    InterviewVO getInterview(Long id);

    /**
     * 查询职位信息
     * @param id 职位id
     * @return 职位信息
     */
    JobVO getJob(Long id);
}
