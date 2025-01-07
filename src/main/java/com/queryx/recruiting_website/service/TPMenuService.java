package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.dto.MenuDto;
import com.queryx.recruiting_website.domain.dto.UpdateMenuDto;
import com.queryx.recruiting_website.domain.vo.MenuListVo;
import com.queryx.recruiting_website.domain.vo.MenuVo;
import com.queryx.recruiting_website.domain.vo.RoutersVo;

import java.util.List;


public interface TPMenuService extends IService<TPMenu> {
    RoutersVo getRouter();

    List<MenuListVo> menuList(String status, String menuName);

    Object addMenu(MenuDto menu);

    MenuListVo menuInfo(Long menuId);

    Object updateMenu(UpdateMenuDto menu);

    Object delMenu(Long menuId);
}

