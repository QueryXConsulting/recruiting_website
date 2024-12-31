package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.vo.RoutersVo;


public interface TPMenuService extends IService<TPMenu> {
    RoutersVo getRouter();
}

