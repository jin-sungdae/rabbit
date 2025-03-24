package com.user.config.security;

import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return user.getUserPassword(); // 실제 비밀번호
    }

    @Override
    public String getUsername() {
        return user.getUserId(); // 로그인 아이디
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 비활성화 정책 없으면 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 정책 없으면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 정책 없으면 true
    }

    @Override
    public boolean isEnabled() {
        return true; // 정지 유저 아닐 경우 true
    }
}