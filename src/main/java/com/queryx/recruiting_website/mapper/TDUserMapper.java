package com.queryx.recruiting_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.queryx.recruiting_website.domain.TDUser;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface TDUserMapper extends BaseMapper<TDUser> {


    TDUser queryUserByEmail(String username);
}

