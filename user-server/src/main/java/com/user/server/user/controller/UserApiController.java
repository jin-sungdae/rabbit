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
    public APIDataResponse<String> createUser(@RequestBody RequestUser requestUser) {

        userService.createUser(requestUser);
        return APIDataResponse.of(Boolean.toString(true));
    }

    @PostMapping(value = "/login")
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

        return APIDataResponse.of(ResponseUser.builder()
                .userId(user.getUserId())
                .role(user.getRole())
                .userName(user.getUserName())
                .build());
    }



    @PostMapping("/logout")
    public APIDataResponse<String> logout(@RequestHeader(value = "Authorization", required = false) String accessTokenHeader,
                                          HttpServletResponse response) {
        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            return APIDataResponse.of("true", "이미 로그아웃 상태(토큰 없음)");
        }

        String accessToken = accessTokenHeader.replace("Bearer ", "");

        try {
            String userId = jwtTokenProvider.parseClaims(accessToken).getSubject();

            // Redis refresh token 삭제
            redisUserRefreshRepository.deleteRefreshToken(userId);

            // Access Token 블랙리스트 등록
            long ttl = jwtTokenProvider.getRemainingExpiration(accessToken);
            if (ttl > 0) {
                blacklistTokenService.addToBlacklist(accessToken, ttl);
            }

            // 클라이언트 쿠키 제거 (HttpOnly는 JS에서 삭제 불가 → 서버가 빈 쿠키로 재설정)
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

}
