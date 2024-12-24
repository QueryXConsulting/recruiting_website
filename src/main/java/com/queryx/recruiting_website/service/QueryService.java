package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.vo.AttachmentsResumeListVO;
import com.queryx.recruiting_website.vo.InterviewVO;
import com.queryx.recruiting_website.vo.ResumeVO;

import java.util.List;

public interface QueryService {
    /**
     * 查询在线简历信息
     * @param id 用户id
     * @return 简历信息
     */
    ResumeVO getOnlineResume(Long id);
    /**
     * 查询在线简历信息
     * @param id 用户id
     * @return 简历信息
     */
    List<AttachmentsResumeListVO> getResumeAttachmentList(Long id);

    /**
     * 查询面试信息
     * @param id 用户id
     * @return 面试信息
     */
    InterviewVO getInterview(Long id);
}
