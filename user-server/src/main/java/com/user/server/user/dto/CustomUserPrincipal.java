package com.user.server.user.dto;

import com.user.server.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
public class CustomUserPrincipal extends DefaultOAuth2User {

    private final User user;

    public CustomUserPrincipal(Collection<? extends GrantedAuthority> authorities,
                               Map<String, Object> attributes,
                               String nameAttributeKey,
                               User user) {
        super(authorities, attributes, nameAttributeKey);
        this.user = user;
    }

    public String getUserId() {
        return user.getUserId();
    }

    public String getRole() {
        return user.getRole().name();
    }

    public String getUserName() {
        return user.getUserName();
    }

    public String getEmail() {
        return user.getEmail();
    }
}