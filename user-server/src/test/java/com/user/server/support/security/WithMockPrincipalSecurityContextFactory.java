package com.user.server.support.security;

import com.user.config.security.PrincipalDetails;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
public class WithMockPrincipalSecurityContextFactory implements WithSecurityContextFactory<WithMockPrincipal> {

    @Autowired
    private ApplicationContext context;

    @Override
    public SecurityContext createSecurityContext(WithMockPrincipal annotation) {
        String uid = annotation.uid();

        UserRepository userRepository = context.getBean(UserRepository.class);

        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("테스트 유저가 존재하지 않음: " + uid));

        PrincipalDetails principalDetails = new PrincipalDetails(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}