package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.vo.*;


/**
 * (TDUser)表服务接口
 *
 * @author makejava
 * @since 2024-12-23 13:10:59
 */
public interface TDUserService extends IService<TDUser> {


    UserCompanyVo selectUserInfo(Long userId, String userRole);

    UserCompanyVo updateUserInfo(UserCompanyVo userCompanyVo);
}

