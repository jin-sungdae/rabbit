package com.user.config.security;

import com.user.server.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        if (user.getUser().getRole() == Role.SELLER) {
            response.sendRedirect("/seller/dashboard");
        } else {
            response.sendRedirect("/products");
        }
    }
}
