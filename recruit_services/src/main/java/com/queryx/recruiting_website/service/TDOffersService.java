package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.vo.ResumeListVO;
import com.queryx.recruiting_website.domain.vo.ResumeManageVO;


public interface TDOffersService extends IService<TDOffers> {


    Object offerList(Integer page, Integer size, Long jobId);

    Object selectOfferTemplate();
}

