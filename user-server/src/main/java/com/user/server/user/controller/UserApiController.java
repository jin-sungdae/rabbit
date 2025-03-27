package com.user.server.user.controller;


import com.common.config.api.apidto.APIDataResponse;
import com.user.config.security.JwtTokenProvider;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.dto.RequestUser;
import com.user.server.user.dto.ResponseUser;
import com.user.server.user.entity.User;
import com.user.server.user.service.AuthService;
import com.user.server.user.service.BlacklistTokenService;
import com.user.server.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserApiController {

    private final UserService userService;
    private final RedisUserRefreshRepository redisUserRefreshRepository;
    private final BlacklistTokenService blacklistTokenService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    @RateLimiter(name = "createUser", fallbackMethod = "rateLimitFallback")
    public APIDataResponse<String> createUser(@RequestBody RequestUser requestUser) {

        userService.createUser(requestUser);
        return APIDataResponse.of(Boolean.toString(true));
    }

    @PostMapping(value = "/login")
    @RateLimiter(name = "login", fallbackMethod = "rateLimitFallback")
    public APIDataResponse<ResponseUser> login(@RequestBody RequestUser requestUser, HttpServletResponse response) {

        // 1. 아이디/비밀번호 검증
        User user = authService.login(requestUser.getUserId(), requestUser.getUserPassword());

        // 2. JWT 액세스 토큰 + 리프레시 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        // 3. Redis에 refreshToken 저장 (로그아웃/토큰 만료 등 관리)
        authService.saveRefreshToken(user.getUserId(), refreshToken);

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofMinutes(2))
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

        return APIDataResponse.of(ResponseUser.builder()
                .userId(user.getUserId())
                .role(user.getRole())
                .userName(user.getUserName())
                .build());
    }


    @PostMapping("/logout")
    public APIDataResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null) {
            return APIDataResponse.of("true", "이미 로그아웃 상태(토큰 없음)");
        }

        try {
            String userId = jwtTokenProvider.parseClaims(accessToken).getSubject();

            // 1. Redis refresh token 삭제
            redisUserRefreshRepository.deleteRefreshToken(userId);

            // 2. Access Token 블랙리스트 등록
            long ttl = jwtTokenProvider.getRemainingExpiration(accessToken);
            if (ttl > 0) {
                blacklistTokenService.addToBlacklist(accessToken, ttl);
            }

            // 3. 클라이언트 쿠키 제거
            ResponseCookie expiredAccess = ResponseCookie.from("accessToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(0)
                    .build();

            ResponseCookie expiredRefresh = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(0)
                    .build();

            response.addHeader("Set-Cookie", expiredAccess.toString());
            response.addHeader("Set-Cookie", expiredRefresh.toString());

            return APIDataResponse.of("true", "로그아웃 성공");
        } catch (Exception e) {
            return APIDataResponse.of("true", "토큰 파싱 실패 - 이미 만료된 경우로 간주하고 클리어");
        }
    }



    @GetMapping("/test")
    public APIDataResponse<String> test() {
        return APIDataResponse.of(Boolean.toString(true));
    }

    public APIDataResponse<String> rateLimitFallback(Exception e) {
        return APIDataResponse.of("false", "요청이 너무 많습니다. 잠시 후 다시 시도하세요.");
    }

}
