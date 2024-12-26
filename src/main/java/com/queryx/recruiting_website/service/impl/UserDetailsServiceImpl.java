package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl {
 /*implements UserDetailsService
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // 查询数据库，根据用户名查询用户信息，并返回UserDetails对象
        LambdaQueryWrapper<TDUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDUser::getUserPhone, phone).eq(TDUser::getUserStatus, 0);
        TDUser user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        return User.withUsername(user.getUserPhone()).authorities("ROLE_USER").build();
    }*/
}
