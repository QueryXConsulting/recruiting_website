package com.queryx.recruiting_website.domain.design;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.TDUserMapper;


public class PhoneLoginStrategy implements LoginStrategy {

    @Override
    public LoginVO login(TDUserMapper userMapper, LoginDTO loginDTO) {
        LoginVO loginVO = new LoginVO();
        // TODO 手机号登录: 验证码待实现
        TDUser user = userMapper.selectOne(new LambdaQueryWrapper<TDUser>()
                .eq(TDUser::getUserPhone, loginDTO.getUsername())
                .eq(TDUser::getUserPassword, loginDTO.getUserPassword())
                .eq(TDUser::getUserRole, loginDTO.getUserRole())
                .eq(TDUser::getDelFlag, Common.NOT_DELETE)
        );
        // 用户不存在
        if (user == null) {
            return null;
        }
        loginVO.setUserId(user.getUserId());
        loginVO.setResumeId(user.getResumeId());
        return loginVO;
    }
}
