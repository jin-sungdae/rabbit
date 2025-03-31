package com.user.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private RedisUserRefreshRepository redisUserRefreshRepository;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // secretKey 등 필요한 변수를 직접 할당하거나,
        // ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "123");
        // 등으로 주입한 뒤, init() 메서드를 수동으로 호출할 수도 있습니다.
        jwtTokenProvider.init(); // @PostConstruct를 수동 호출
    }

    @Test
    @DisplayName("AccessToken 생성 시, 만료 시간이 15분 후로 설정된다")
    void createAccessToken_ExpirationTime() {
        // given
        User user = User.builder()
                .userId("testUser")
                // role, userName, uid 등 설정
                .build();

        // when
        String accessToken = jwtTokenProvider.createAccessToken(user);

        // then
        DecodedJWT decodedJWT = JWT.decode(accessToken);
        // JWT.decode() 를 사용해, exp 클레임이 현재 시간 + 15분인지 확인
        Date expiresAt = decodedJWT.getExpiresAt();
        long diffMillis = expiresAt.getTime() - System.currentTimeMillis();

        // diffMillis 가 15분 근처인지(오차 범위 허용) 확인
        assertTrue(diffMillis > 14 * 60_000 && diffMillis <= 15 * 60_000);
        assertEquals("testUser", decodedJWT.getClaim("userId").asString());
    }

    @Test
    @DisplayName("parseClaims - 정상 토큰이면 DecodedJWT를 반환")
    void parseClaims_ValidToken() {
        // given
        // AccessToken 하나 발급
        String validToken = jwtTokenProvider.createAccessToken("testUser", "USER", "홍길동", "UID1234");

        // when
        DecodedJWT decodedJWT = jwtTokenProvider.parseClaims(validToken);

        // then
        assertNotNull(decodedJWT);
        assertEquals("testUser", decodedJWT.getClaim("userId").asString());
        assertEquals("USER", decodedJWT.getClaim("roles").asString());
    }

    @Test
    @DisplayName("parseClaims - 서명이 달라진 토큰이면 RuntimeException 발생")
    void parseClaims_InvalidSignature() {
        // given: 서명이 다른 토큰
        // HMAC512("wrongSecretKey") 로 임의로 만든 가짜 토큰
        String invalidSignatureToken = JWT.create()
                .withClaim("userId", "testUser")
                .sign(Algorithm.HMAC512("wrongSecretKey"));

        // when & then
        assertThrows(RuntimeException.class, () -> jwtTokenProvider.parseClaims(invalidSignatureToken));
    }

    @Test
    @DisplayName("isExpired - 만료된 토큰이면 true 반환")
    void isExpired_ExpiredToken() {
        // given: 이미 만료된 토큰(0초 유효기간 예시)
        String expiredToken = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000)) // 과거 시점
                .sign(Algorithm.HMAC512("123"));

        // when
        boolean result = jwtTokenProvider.isExpired(expiredToken);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("validateRefreshToken - Redis에 저장된 RefreshToken과 일치하면 true")
    void validateRefreshToken_Valid() throws JsonProcessingException {
        // given
        String userId = "testUser";
        String refreshToken = JWT.create()
                .withSubject(userId)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L))
                .sign(Algorithm.HMAC512("123"));

        given(redisUserRefreshRepository.getRefreshToken(userId)).willReturn(refreshToken);

        // when
        boolean result = jwtTokenProvider.validateRefreshToken(refreshToken);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("validateRefreshToken - Redis에 토큰이 없거나 다른 토큰이면 false")
    void validateRefreshToken_Invalid() throws JsonProcessingException {
        // given
        String userId = "testUser";
        String refreshToken = JWT.create()
                .withSubject(userId)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L))
                .sign(Algorithm.HMAC512("123"));

        // Redis에 저장된 토큰이 없거나 다른 문자열이 저장됨
        given(redisUserRefreshRepository.getRefreshToken(userId)).willReturn(null);

        // when
        boolean result = jwtTokenProvider.validateRefreshToken(refreshToken);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("getUserIdFromRefreshToken - 토큰에서 userId를 추출한다")
    void getUserIdFromRefreshToken() {
        // given
        String userId = "testUser";
        String refreshToken = JWT.create()
                .withSubject(userId)
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512("123"));

        // when
        String result = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);

        // then
        assertEquals(userId, result);
    }
}