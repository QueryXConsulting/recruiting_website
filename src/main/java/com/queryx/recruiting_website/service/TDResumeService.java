package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.vo.ResumeListVo;
import com.queryx.recruiting_website.domain.vo.ResumeManageVo;


public interface TDResumeService extends IService<TDResume> {
    Page<ResumeListVo> selectResumeList(Integer page, Integer size,Long companyId);

    Object selectResume(SelectResumeDto selectResumeDto);

    Page<ResumeManageVo> selectResumeManage(Integer page, Integer size, String userName, String resumeReview, String resumeStatus, String resumeType);

    Object resumeReview(String review, Long resumeId, String resumeType);
}

