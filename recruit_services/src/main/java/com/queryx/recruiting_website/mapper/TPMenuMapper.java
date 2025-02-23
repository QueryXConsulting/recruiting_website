package com.queryx.recruiting_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.queryx.recruiting_website.domain.TPMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface TPMenuMapper extends BaseMapper<TPMenu> {
    List<String> selectPermsByRoleId(Long roleId);
}

