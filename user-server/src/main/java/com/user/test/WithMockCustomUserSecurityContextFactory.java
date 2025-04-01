package com.user.test;

import com.user.config.security.PrincipalDetails;
import com.user.server.user.entity.SellerProfile;
import com.user.server.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SellerProfile sellerProfile = SellerProfile.builder()
                .id(annotation.sellerId())
                .build();

        User user = User.builder()
                .id(annotation.id())
                .userName(annotation.username())
                .userPassword(annotation.password())
                .role(annotation.role())
                .sellerProfile(sellerProfile)
                .build();

        PrincipalDetails principal = new PrincipalDetails(user);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
