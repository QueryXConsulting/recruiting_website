package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TPRole;
import com.queryx.recruiting_website.domain.dto.RoleInfoDto;
import com.queryx.recruiting_website.domain.vo.RoleListVO;
import com.queryx.recruiting_website.domain.vo.RoleVO;

import java.util.List;


public interface TPRoleService extends IService<TPRole> {


    List<RoleListVO> selectRoleList();

    RoleInfoDto updateRoleInfo(RoleInfoDto roleInfoDto);

    String updateRoleStatus(Long roleId,String roleStatus);

    RoleVO roleInfo(Long roleId);

    Object addRole(RoleInfoDto roleInfoDto);

    Object delRole(List<Long> roleId);

    Object selectRoleMenusTree();
}

