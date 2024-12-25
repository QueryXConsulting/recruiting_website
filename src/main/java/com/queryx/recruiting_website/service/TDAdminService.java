package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.vo.*;


/**
 * (TDAdmin)表服务接口
 *
 * @author makejava
 * @since 2024-12-23 13:10:59
 */
public interface TDAdminService extends IService<TDAdmin> {

    AdminVo addAdmin(AdminVo adminVo);

    Object login(AdminLoginVo adminLoginVo);



}

