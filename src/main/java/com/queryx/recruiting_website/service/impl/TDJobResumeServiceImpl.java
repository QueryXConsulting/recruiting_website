package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.service.TDJobResumeService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class TDJobResumeServiceImpl extends ServiceImpl<TDJobResumeMapper, TDJobResume> implements TDJobResumeService {


}
