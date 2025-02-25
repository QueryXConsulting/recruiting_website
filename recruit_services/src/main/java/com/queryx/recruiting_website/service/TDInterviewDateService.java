package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDInterviewDate;
import com.queryx.recruiting_website.domain.TDJobNature;
import com.queryx.recruiting_website.domain.dto.AddInterviewDto;

public interface TDInterviewDateService extends IService<TDInterviewDate> {


    Object addInterviewDate(AddInterviewDto addInterviewDto);

    Object selectInterviewDate();
}
