package com.user.server.support.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockPrincipalSecurityContextFactory.class)
public @interface WithMockPrincipal {
    String uid() default "uid-test-123";
}