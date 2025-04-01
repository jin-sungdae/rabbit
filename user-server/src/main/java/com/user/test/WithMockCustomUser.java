package com.user.test;

import com.user.server.user.entity.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    long id() default 1L;
    String username() default "testUser";
    String password() default "encodedPassword";
    long sellerId() default 1L;
    Role role() default Role.USER;
}
