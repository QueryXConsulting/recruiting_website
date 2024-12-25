package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.service.TDResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service("tDResumeService")
@RequiredArgsConstructor
public class TDResumeServiceImpl extends ServiceImpl<TDResumeMapper, TDResume> implements TDResumeService {

}
