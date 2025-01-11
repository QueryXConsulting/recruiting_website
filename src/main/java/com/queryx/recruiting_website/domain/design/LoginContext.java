package com.queryx.recruiting_website.domain.design;

import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.UserMapper;

public class LoginContext {
    private LoginStrategy loginStrategy;

    final static String PHONE = "(^1[3-9]\\d{9}$)";
    final static String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";


    public static LoginVO executeLogin(UserMapper mapper, LoginDTO dto) {
        LoginContext context = new LoginContext();
        if (dto.getUsername().matches(PHONE)) {
            context.loginStrategy = new PhoneLoginStrategy();
        }
        else if (dto.getUsername().matches(EMAIL)) {
            context.loginStrategy = new EmailLoginStrategy();
        } else {
            return null;
        }
        return context.loginStrategy.login(mapper, dto);
    }
}
