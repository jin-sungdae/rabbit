package com.user.config.security;

import com.user.server.redis.RedisLoginFailRepository;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.dto.CustomUserPrincipal;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.service.AuthService;
import com.user.server.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${front.url}")
    private String frontUrl;

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisLoginFailRepository redisLoginFailRepository;
    private final RedisUserRefreshRepository redisUserRefreshRepository;
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);
    private final UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        // JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(principal.getUserId());

        // Redis 저장 등 처리
        redisLoginFailRepository.clearFailCount(principal.getUserId());
        redisUserRefreshRepository.saveRefreshToken(principal.getUserId(), refreshToken, REFRESH_TOKEN_EXPIRATION);

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 4. user info redis 에 저장
        userService.saveUserInfoByCache(user.getUid());

        getRedirectStrategy().sendRedirect(request, response, frontUrl + "/home");
    }
}
