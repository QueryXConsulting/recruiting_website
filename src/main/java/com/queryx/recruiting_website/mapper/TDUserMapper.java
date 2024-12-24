package com.queryx.recruiting_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.vo.JobCompanyListVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (TDUser)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-23 13:11:03
 */
@Mapper
public interface TDUserMapper extends BaseMapper<TDUser> {



}

