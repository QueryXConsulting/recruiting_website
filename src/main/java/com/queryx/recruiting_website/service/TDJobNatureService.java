package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDJobNature;
import com.queryx.recruiting_website.domain.dto.JobNatureDto;
import com.queryx.recruiting_website.domain.vo.NatureVO;

import java.util.List;

public interface TDJobNatureService extends IService<TDJobNature> {

    List<NatureVO> selectJobNatureList(String status);

    NatureVO selectNatureInfo(Long jobNatureId);

    Object updateJobNatureStatus(Integer status, Long jobNatureId);

    Object updateJobNature(JobNatureDto jobNatureDto);


    Object addJobNature(String jobNatureName, String natureStatus);

    Object delJobNature(Long jobNatureId);
}

