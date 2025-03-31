package com.user.server.config.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.user.config.security.JwtAuthorizationFilter;
import com.user.config.security.JwtTokenProvider;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthorizationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisUserRefreshRepository redisUserRefreshRepository;

    @InjectMocks
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    @DisplayName("토큰이 없는 경우 401 UNAUTHORIZED 응답")
    void testDoFilterInternal_NoToken_ReturnUnauthorized() throws ServletException, IOException {
        // given: 토큰이 없는 상황
        // (Authorization 헤더나 쿠키에 accessToken이 없음)

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        // SC_UNAUTHORIZED(401)로 응답해야 한다
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    /**
     * 유효한 AccessToken을 통해 요청이 전달되었을 때,
     * JwtAuthorizationFilter가 정상적으로 인증 과정을 수행하여
     * SecurityContextHolder에 {@link Authentication} 객체를 설정하는지 검증합니다.
     *
     * 시나리오:
     * 유효한 AccessToken("validToken")을 Authorization 헤더에 포함해서 요청을 보냄
     * {@code JwtTokenProvider.isExpired("validToken")} 결과가 false, 즉 만료되지 않은 토큰임을 가정
     * {@link DecodedJWT}와 그 내부 {@link com.auth0.jwt.interfaces.Claim}들을 Mock으로 설정하여 필요한 Claim 값("userId", "roles", "userName", "uid")을 임의로 반환
     * 토큰 파싱 후 {@code SecurityContextHolder}에 인증 정보를 설정
     * 필터 체인 호출이 성공적으로 진행되어, 응답 상태 코드가 200(OK)인지 확인
     * {@code SecurityContextHolder.getContext().getAuthentication()}가 null이 아님을 확인함으로써 인증 처리 성공 확인
     *
     * @throws ServletException 필터 체인 처리 중 서블릿 예외가 발생한 경우
     * @throws IOException      입출력 예외가 발생한 경우
     */
    @Test
    @DisplayName("유효한 AccessToken ⇒ SecurityContextHolder에 Authentication 객체가 설정된다")
    void testDoFilterInternal_ValidAccessToken_AuthenticationSet() throws ServletException, IOException {
        // given
        String token = "Bearer validToken";
        request.addHeader("Authorization", token);

        given(jwtTokenProvider.isExpired("validToken")).willReturn(false);

        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        Claim userIdClaim = mock(Claim.class);
        given(userIdClaim.asString()).willReturn("testUserId");
        given(decodedJWT.getClaim("userId")).willReturn(userIdClaim);

        Claim rolesClaim = mock(Claim.class);
        given(rolesClaim.asString()).willReturn("USER");
        given(decodedJWT.getClaim("roles")).willReturn(rolesClaim);

        Claim userNameClaim = mock(Claim.class);
        given(userNameClaim.asString()).willReturn("홍길동");
        given(decodedJWT.getClaim("userName")).willReturn(userNameClaim);

        Claim uidClaim = mock(Claim.class);
        given(uidClaim.asString()).willReturn("UID1234");
        given(decodedJWT.getClaim("uid")).willReturn(uidClaim);

        given(jwtTokenProvider.parseClaims("validToken")).willReturn(decodedJWT);

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 만료된 AccessToken과 유효한 RefreshToken이 주어졌을 때,
     * 새 AccessToken과 RefreshToken을 재발급하여 응답 헤더/쿠키에 반영하고,
     * SecurityContextHolder에 인증 정보를 설정하는지 검증하는 테스트입니다.
     *
     * 시나리오:
     * 기존 AccessToken이 만료된 상태로 요청이 들어옴
     * 유효한 RefreshToken이 존재할 경우 새 AccessToken/RefreshToken을 생성
     * 응답 헤더(Authorization)에 새 AccessToken을 넣고, 쿠키에 새 RefreshToken을 담아 반환
     * SecurityContextHolder에 인증 정보를 설정해 인증된 상태로 유지
     *
     * @throws ServletException 필터 체인 처리 중 서블릿 에러가 발생한 경우
     * @throws IOException      입출력 에러가 발생한 경우
     */
    @Test
    @DisplayName("만료된 AccessToken + 유효한 RefreshToken => 새 AccessToken 발급 및 인증 정보 설정")
    void testDoFilterInternal_ExpiredAccessToken_WithValidRefreshToken_GenerateNewToken() throws ServletException, IOException {
        // given
        String accessToken = "Bearer expiredAccess";
        request.addHeader("Authorization", accessToken);

        Cookie refreshCookie = new Cookie("refreshToken", "validRefresh");
        request.setCookies(refreshCookie);

        given(jwtTokenProvider.isExpired("expiredAccess")).willReturn(true);
        given(jwtTokenProvider.validateRefreshToken("validRefresh")).willReturn(true);
        given(jwtTokenProvider.getUserIdFromRefreshToken("validRefresh")).willReturn("testUserId");

        User user = User.builder().userId("testUserId").build();
        given(userRepository.findByUserId("testUserId")).willReturn(java.util.Optional.of(user));

        given(jwtTokenProvider.createAccessToken(user)).willReturn("newAccessToken");
        given(jwtTokenProvider.createRefreshToken(user)).willReturn("newRefreshToken");

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertEquals("Bearer newAccessToken", response.getHeader("Authorization"));
        Cookie[] cookies = response.getCookies();
        assertTrue(
                java.util.Arrays.stream(cookies)
                        .anyMatch(c -> "refreshToken".equals(c.getName()) && "newRefreshToken".equals(c.getValue()))
        );
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}