package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.TPRole;
import com.queryx.recruiting_website.domain.dto.AdminDto;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;
import com.queryx.recruiting_website.domain.dto.RoleInfoDto;
import com.queryx.recruiting_website.domain.vo.AdminUserInfoVo;
import com.queryx.recruiting_website.domain.vo.RoleListVo;
import com.queryx.recruiting_website.domain.vo.RoleVo;
import com.queryx.recruiting_website.utils.CommonResp;

import java.util.List;


public interface TPRoleService extends IService<TPRole> {


    List<RoleListVo> selectRoleList();

    RoleInfoDto updateRoleInfo(RoleInfoDto roleInfoDto);

    String updateRoleStatus(Long roleId,String roleStatus);

    RoleVo roleInfo(Long roleId);

    Object addRole(RoleInfoDto roleInfoDto);

    Object delRole(Long roleId);
}

