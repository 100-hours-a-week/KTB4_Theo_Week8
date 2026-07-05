package com.theo.community_api.auth.security;

import com.theo.community_api.user.domain.User;
import com.theo.community_api.user.domain.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

// Spring Security 인증에 필요한 사용자 정보만 보관하는 전용 principal 객체
public final class CustomUserDetails implements UserDetails {

    @Getter
    private final Long userId;
    private final String email;
    private final String password;
    private final UserRole role;
    private final boolean enabled;

    private CustomUserDetails(
            Long userId,
            String email,
            String password,
            UserRole role,
            boolean enabled // 삭제된 회원인지 확인하는 필드
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public static CustomUserDetails from(User user) {
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                !user.isDeleted()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
