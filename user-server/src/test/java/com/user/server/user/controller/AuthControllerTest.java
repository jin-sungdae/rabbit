package com.user.server.user.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.common.config.api.controller.APIExceptionHandler;
import com.user.config.security.JwtTokenProvider;
import com.user.server.redis.RedisUserInfoRepository;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.service.UserService;


import com.user.test.WithMockCustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(APIExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisUserRefreshRepository redisUserRefreshRepository;

    @MockBean
    private RedisUserInfoRepository redisUserInfoRepository;

    @DisplayName("[정상 테스트] 요청이 들어왔을때 Refresh Token으로 Access Token 재발급")
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
    @Test
    void test_refreshToken_success() throws Exception {
        // given
        String refreshToken = "valid-refresh-token";
        String userId = "123";
        String newAccessToken = "new-access-token";

        // DecodedJWT Stub 객체 생성
        Claim claim = mock(Claim.class);
        given(claim.asString()).willReturn(userId);

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        given(decodedJWT.getSubject()).willReturn(userId);
        given(decodedJWT.getClaim("userId")).willReturn(claim);

        given(jwtTokenProvider.parseClaims(refreshToken)).willReturn(decodedJWT);
        given(redisUserRefreshRepository.getRefreshToken(userId)).willReturn(refreshToken);

        User user = User.builder().id(Long.parseLong(userId)).userName("tester").build();
        given(userService.getUserById(userId)).willReturn(user);
        given(jwtTokenProvider.createAccessToken(user)).willReturn(newAccessToken);

        // when & then
        mockMvc.perform(post("/refresh")
                        .with(csrf())
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("accessToken", newAccessToken))
                .andExpect(jsonPath("$.data").value("AccessToken refreshed"));
    }

    @Test
    @DisplayName("[실패 테스트] RefreshToken 쿠키가 없으면 예외 발생")
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
    void test_refreshToken_noCookie() throws Exception {
        mockMvc.perform(post("/refresh"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[실패 테스트] Redis 저장된 토큰과 다르면 예외")
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
    void test_refreshToken_mismatch() throws Exception {
        String refreshToken = "hacked-token";
        String userId = "123";
        String newAccessToken = "new-access-token";

        Claim claim = mock(Claim.class);
        given(claim.asString()).willReturn(userId);

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        given(decodedJWT.getSubject()).willReturn(userId);
        given(decodedJWT.getClaim("userId")).willReturn(claim);

        given(jwtTokenProvider.parseClaims(refreshToken)).willReturn(decodedJWT);
        given(redisUserRefreshRepository.getRefreshToken(userId)).willReturn("valid-token");

        mockMvc.perform(post("/refresh")
                        .with(csrf())
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().is5xxServerError());
    }
}