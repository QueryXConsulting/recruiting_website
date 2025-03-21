package com.queryx.recruiting_website.domain.design;

import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import org.springframework.beans.BeanUtils;

public class SearchJobStrategy implements SearchStrategy {

    @Override
    public LoginVO login(TDUserMapper userMapper, LoginDTO loginDTO) {
        // TODO 邮箱登录：验证码待实现
        TDUser user = userMapper.queryUserByEmail(loginDTO.getUsername());
        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(user, loginVO);
        return loginVO;
    }
}
