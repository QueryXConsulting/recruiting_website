package com.queryx.recruiting_website.domain.design;

import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.UserMapper;

public class EmailLoginStrategy implements LoginStrategy {

    @Override
    public LoginVO login(UserMapper userMapper, LoginDTO loginDTO) {
        // TODO 邮箱登录：验证码待实现
        return userMapper.queryUserByEmail(loginDTO);
    }
}
