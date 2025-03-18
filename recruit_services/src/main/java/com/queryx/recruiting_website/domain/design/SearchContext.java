package com.queryx.recruiting_website.domain.design;

import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.impl.UserServiceImpl;

public class SearchContext {
    private SearchStrategy loginStrategy;



    public static LoginVO executeLogin(String keyword, int page, int size, LoginDTO dto) {
        return null;
    }
}
