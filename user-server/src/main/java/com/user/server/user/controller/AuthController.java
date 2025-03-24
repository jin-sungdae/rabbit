package com.user.server.user.controller;


import com.common.config.api.apidto.APIDataResponse;
import com.common.config.api.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.config.security.JwtTokenProvider;
import com.user.config.security.PrincipalDetails;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.dto.ResponseUser;
import com.user.server.user.entity.User;
import com.user.server.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUserRefreshRepository redisUserRefreshRepository;

    @PostMapping("/refresh")
    public APIDataResponse<String> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                                HttpServletResponse response) throws JsonProcessingException {

        // 1. 쿠키에 refreshToken이 존재하는지 확인
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new GeneralException("Refresh Token이 존재하지 않거나 비어 있습니다.");
        }

        // 2. Refresh Token 검증 및 userId 추출
        String userId;
        try {
            userId = jwtTokenProvider.parseClaims(refreshToken).getSubject();
        } catch (Exception e) {
            throw new GeneralException("Refresh Token 검증 실패");
        }

        // 3. Redis 저장된 refreshToken과 일치 확인
        String savedToken = redisUserRefreshRepository.getRefreshToken(userId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new GeneralException("Refresh Token이 일치하지 않습니다.");
        }

        // 4. 유저 정보로 새 accessToken 발급
        User user = userService.getUserById(userId);
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        // 5. accessToken을 HttpOnly 쿠키로 발급
        ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader("Set-Cookie", newAccessCookie.toString());

        // 6. 최소한의 메시지 응답
        return APIDataResponse.of("AccessToken refreshed", "새로운 Access Token 발급 성공");
    }

    @GetMapping("/me")
    public APIDataResponse<ResponseUser> getMyInfo(@AuthenticationPrincipal PrincipalDetails principal) {
        User user = principal.getUser();
        return APIDataResponse.of(
                ResponseUser.builder()
                        .userId(user.getUserId())
                        .userName(user.getUserName())
                        .role(user.getRole())
                        .build()
        );
    }

}
