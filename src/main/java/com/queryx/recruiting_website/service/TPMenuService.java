package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.dto.MenuDto;
import com.queryx.recruiting_website.domain.dto.UpdateMenuDto;
import com.queryx.recruiting_website.domain.vo.MenuListVO;
import com.queryx.recruiting_website.domain.vo.RoutersVO;

import java.util.List;


public interface TPMenuService extends IService<TPMenu> {
    RoutersVO getRouter();

    List<MenuListVO> menuList();

    Object addMenu(MenuDto menu);

    MenuListVO menuInfo(Long menuId);

    Object updateMenu(UpdateMenuDto menu);

    Object delMenu(Long menuId);
}

