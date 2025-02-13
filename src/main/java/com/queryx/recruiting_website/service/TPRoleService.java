package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TPRole;
import com.queryx.recruiting_website.domain.dto.RoleInfoDTO;
import com.queryx.recruiting_website.domain.vo.RoleListVO;
import com.queryx.recruiting_website.domain.vo.RoleVO;

import java.util.List;


public interface TPRoleService extends IService<TPRole> {


    List<RoleListVO> selectRoleList();

    RoleInfoDTO updateRoleInfo(RoleInfoDTO roleInfoDTO);

    String updateRoleStatus(Long roleId,String roleStatus);

    RoleVO roleInfo(Long roleId);

    Object addRole(RoleInfoDTO roleInfoDTO);

    Object delRole(Long roleId);
}

