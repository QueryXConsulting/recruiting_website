package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.vo.ResumeListVo;
import com.queryx.recruiting_website.domain.vo.ResumeVo;

import java.util.List;



public interface TDResumeService extends IService<TDResume> {
    List<ResumeListVo> insertJobInfo(Long companyId);

    ResumeVo selectResume(SelectResumeDto selectResumeDto);
}

