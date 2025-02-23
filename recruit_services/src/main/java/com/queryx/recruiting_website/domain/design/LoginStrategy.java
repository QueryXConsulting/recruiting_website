package com.queryx.recruiting_website.domain.design;

import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.TDUserMapper;

public interface LoginStrategy {
    LoginVO login(TDUserMapper userMapper, LoginDTO loginDTO);
}
