package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.queryx.recruiting_website.domain.TDUser;

import com.queryx.recruiting_website.mapper.TDUserMapper;

import com.queryx.recruiting_website.service.TDUserService;

import org.springframework.stereotype.Service;



/**
 * (TDUser)表服务实现类
 *
 * @author makejava
 * @since 2024-12-23 13:11:00
 */
@Service("tDUserService")
public class TDUserServiceImpl extends ServiceImpl<TDUserMapper, TDUser> implements TDUserService {

}


