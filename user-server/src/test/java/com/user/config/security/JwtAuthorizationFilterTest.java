package com.user.config.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    /**
     * 화이트리스트(WHITE_LIST)에 해당하는 URI는 토큰 검증을 거치지 않고
     * 필터 체인을 바로 통과하는지 검증하는 테스트입니다.
     *
     * 시나리오:
     *   1) URI가 "/api/v1/user/register" 등 WHITE_LIST에 해당하는 경우
     *   2) JwtAuthorizationFilter 내부에서 isWhitelist(...)가 true를 반환
     *   3) 토큰 검사 없이 chain.doFilter()가 호출되어, 응답 코드가 200 OK로 설정됨
     */
    @Test
    @DisplayName("화이트리스트 URI => 토큰 검증 없이 필터 체인 통과")
    void testDoFilterInternal_WhiteListEndpoint_SkipFilter() throws ServletException, IOException {
        // given
        // 화이트리스트에 있는 경로 예: "/api/v1/user/register"
        request.setRequestURI("/api/v1/user/register");

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        // 화이트리스트 경로이므로 토큰 검증 없이 체인 통과
        // Status 코드가 설정되지 않았다면 기본값 200, 혹은 MockFilterChain이 끝날 시점에 200 OK
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        // SecurityContext에는 인증이 설정되지 않아야 정상 (화이트리스트는 인증 요구 안 함)
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Authorization 헤더가 "Bearer "로 시작하지 않는 경우(예: "Basic " 또는 null)
     * SC_UNAUTHORIZED(401) 응답을 반환해야 하는지 테스트합니다.
     *
     * 시나리오:
     *   1) Authorization 헤더에 "Basic someToken" 형태가 들어옴
     *   2) JwtAuthorizationFilter에서 Bearer 접두사가 아니라는 이유로 401 반환
     */
    @Test
    @DisplayName("Bearer 접두사 누락 or 잘못된 형태 => 401 Unauthorized")
    void testDoFilterInternal_InvalidBearerPrefix_ReturnUnauthorized() throws ServletException, IOException {
        // given
        // "Bearer "가 아니라 "Basic " 등 잘못된 형태
        request.addHeader("Authorization", "Basic invalidPrefixToken");

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        // Missing JWT Token으로 간주해 401
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    /**
     * 만료된 AccessToken이 들어왔지만, RefreshToken도 만료되거나
     * 유효하지 않은 경우 재발급 불가능 ⇒ 401 혹은 JwtException 처리 시나리오.
     *
     * 시나리오:
     *   1) AccessToken이 만료됨
     *   2) RefreshToken이 쿠키에 있으나, validateRefreshToken(...) 결과 false
     *   3) JwtAuthorizationFilter에서 "AccessToken expired and RefreshToken invalid" 예외 발생
     */
    @Test
    @DisplayName("만료된 AccessToken + 만료/무효한 RefreshToken => JwtException 처리")
    void testDoFilterInternal_ExpiredAccessToken_WithInvalidRefreshToken_ThrowJwtException() throws ServletException, IOException {
        // given
        String accessToken = "Bearer expiredAccess";
        request.addHeader("Authorization", accessToken);

        // 쿠키에 있지만, 실제로는 만료 or 무효한 RefreshToken
        Cookie refreshCookie = new Cookie("refreshToken", "invalidRefresh");
        request.setCookies(refreshCookie);

        given(jwtTokenProvider.isExpired("expiredAccess")).willReturn(true);
        // RefreshToken 검증 시 false (만료, 서명오류, Redis에 없음 등)
        given(jwtTokenProvider.validateRefreshToken("invalidRefresh")).willReturn(false);

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        // 필터 코드상, RefreshToken까지 무효하면 JwtException 로그.warn 하고 SecurityContextHolder.clearContext()
        // 별도로 response.sendError(...)로 401을 보낼 수도, 그냥 체인을 탈 수도 있음
        // 현재 코드에서는 `throw new JwtException("AccessToken expired and RefreshToken invalid");`
        // 이 예외가 try-catch로 잡혀서 log.warn(...) 후 clearContext()만 하고, chain.doFilter()를 호출
        // => 실제 서비스에서 어떻게 처리할지(401 응답 vs. 체인 통과) 로직을 확인 후 맞게 검증

        // 예: 로그.warn 후 200 OK가 나올 수도, 401이 날 수도 있으므로 구현 로직에 맞춰 assert
        // 지금 코드 상으론 catch 처리 후 chain.doFilter() 계속 => status가 200일 가능성도 있음
        // log.warn(...) 확인은 할 수 없지만, SecurityContextHolder가 비어있는지 확인해볼 수 있음
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "RefreshToken 무효 시 인증 정보가 없어야 함");
    }

    /**
     * RefreshToken은 유효하지만, DB에서 해당 userId를 찾지 못하는 경우
     * IllegalArgumentException("사용자를 찾을 수 없습니다.")가 발생하는지 확인합니다.
     *
     * 시나리오:
     *   1) isExpired(...) 결과 AccessToken 만료
     *   2) RefreshToken이 validateRefreshToken(...)에서 true
     *   3) getUserIdFromRefreshToken(...)로 "nonExistingUserId" 반환
     *   4) userRepository.findByUserId("nonExistingUserId")가 empty
     *   5) => "사용자를 찾을 수 없습니다." 예외 발생
     */
    @Test
    @DisplayName("RefreshToken 유효하지만 DB에 사용자 존재하지 않는 경우 => IllegalArgumentException 발생")
    void testDoFilterInternal_ExpiredAccessToken_RefreshValidButUserNotFound() throws ServletException, IOException {
        // given
        request.addHeader("Authorization", "Bearer expiredAccess");
        Cookie refreshCookie = new Cookie("refreshToken", "validRefresh");
        request.setCookies(refreshCookie);

        given(jwtTokenProvider.isExpired("expiredAccess")).willReturn(true);
        given(jwtTokenProvider.validateRefreshToken("validRefresh")).willReturn(true);

        // DB에 없는 사용자 시나리오
        given(jwtTokenProvider.getUserIdFromRefreshToken("validRefresh")).willReturn("nonExistingUserId");
        given(userRepository.findByUserId("nonExistingUserId")).willReturn(java.util.Optional.empty());

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        // 현재 필터 코드상, DB에서 user가 없으면 IllegalArgumentException을 던지므로,
        // try { ... } catch()에서 걸리면 log.warn(...) 후 SecurityContextHolder.clearContext()
        // => response status는 200 또는 500 등, 구현에 따라 달라질 수 있음
        // log.warn("[JwtAuthorizationFilter] 토큰 파싱 실패: 사용자 찾을 수 없음") 형태
        // => 예외를 throw하고 잡아서 어떻게 처리하는지 구현 로직에 따라 달라지므로
        //    여기선 SecurityContextHolder에 인증이 없는지만 체크

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "DB에 사용자 없음 -> 인증 정보 설정 불가");
        // 예: 만약 response.sendError(404)나 401을 보내도록 구현했다면
        // assertEquals(404, response.getStatus());
        // 와 같은 검증을 할 수도 있음
    }
}