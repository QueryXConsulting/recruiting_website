package com.queryx.recruiting_website.domain.design;

import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.impl.UserServiceImpl;

public class LoginContext {
    private LoginStrategy loginStrategy;



    public static LoginVO executeLogin(TDUserMapper mapper, LoginDTO dto) {
        LoginContext context = new LoginContext();
        if (dto.getUsername().matches(UserServiceImpl.PHONE)) {
            context.loginStrategy = new PhoneLoginStrategy();
        }
        else if (dto.getUsername().matches(UserServiceImpl.EMAIL)) {
            context.loginStrategy = new EmailLoginStrategy();
        } else {
            return null;
        }
        return context.loginStrategy.login(mapper, dto);
    }
}
