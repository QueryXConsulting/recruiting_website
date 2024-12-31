package com.queryx.recruiting_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;

public interface UserMapper extends BaseMapper<TDUser> {
    LoginVO queryUserByEmail(LoginDTO loginDTO);
}
