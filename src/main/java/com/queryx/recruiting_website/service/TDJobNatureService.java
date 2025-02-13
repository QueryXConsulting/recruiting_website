package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDJobNature;
import com.queryx.recruiting_website.domain.dto.JobNatureDTO;
import com.queryx.recruiting_website.domain.vo.NatureVO;

import java.util.List;

public interface TDJobNatureService extends IService<TDJobNature> {

    List<NatureVO> selectJobNatureList();

    NatureVO selectNatureInfo(Long jobNatureId);

    Object updateJobNatureStatus(Integer status, Long jobNatureId);

    Object updateJobNature(JobNatureDTO jobNatureDTO);


    Object addJobNature(String jobNatureName);

    Object delJobNature(Long jobNatureId);
}

