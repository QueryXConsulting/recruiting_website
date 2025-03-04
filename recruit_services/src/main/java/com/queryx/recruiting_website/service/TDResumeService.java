package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.dto.AddInterviewDto;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.vo.ResumeListVO;
import com.queryx.recruiting_website.domain.vo.ResumeManageVO;


public interface TDResumeService extends IService<TDResume> {
    Page<ResumeListVO> selectResumeList(Integer page, Integer size, Long companyId, String resumeType, String resumeName, String resumeStatus);

    Object selectResume(SelectResumeDto selectResumeDto);

    Page<ResumeManageVO> selectResumeManage(Integer page, Integer size, String userName, String resumeReview, String resumeStatus, String resumeType);

    Object resumeReview(String review, Long resumeId, String resumeType);


    Object updateResumeStatus(String resumeStatus, Long jobResumeId, String resumeDelete);


}

