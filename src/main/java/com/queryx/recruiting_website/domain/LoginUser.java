package com.queryx.recruiting_website.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements UserDetails {

    private TDUser tdUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 普通用户不需要权限
        return null;
    }

    @Override
    public String getPassword() {
        return tdUser.getUserPassword();
    }

    @Override
    public String getUsername() {
        return tdUser.getUserPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
