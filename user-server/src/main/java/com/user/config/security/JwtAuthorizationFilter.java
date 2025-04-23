package com.user.config.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import com.user.server.user.service.BlacklistTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisUserRefreshRepository redisUserRefreshRepository;
    private final BlacklistTokenService blacklistTokenService;

    private static final List<String> WHITE_LIST = List.of(
            "/public/",
            "/login",
            "/logout",
            "/oauth2",
            "/login/oauth2/code/",
            "/refresh",
            "/api/v1/user/login",
            "/api/v1/user/register",
            "/favicon.ico",
            "/api/v1/common/"
    );

    private boolean isWhitelist(String uri) {
        return WHITE_LIST.stream().anyMatch(uri::startsWith);
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String uri = request.getRequestURI();

        if (isWhitelist(uri)) {
            chain.doFilter(request, response);
            return;
        }

        String jwtHeader = resolveTokenFromRequest(request);

        if (jwtHeader == null || !jwtHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT Token");
            return;
        }

        try {
            String token = jwtHeader.replace("Bearer ", "");

            String jti = jwtTokenProvider.getJti(token);
            if (blacklistTokenService.isBlacklisted(jti)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token blacklisted");
                return;
            }

            if (jwtTokenProvider.isExpired(token)) {
                String refreshToken = resolveRefreshTokenFromRequest(request);

                if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                    String userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);



                    User user = userRepository.findByUserId(userId).orElseThrow(
                            () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                    );

                    String newAccessToken = jwtTokenProvider.createAccessToken(user);
                    response.setHeader("Authorization", "Bearer " + newAccessToken);

                    PrincipalDetails principalDetails = new PrincipalDetails(user);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            principalDetails, null, principalDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String newRefreshToken = jwtTokenProvider.createRefreshToken(user);
                    redisUserRefreshRepository.saveRefreshToken(userId, newRefreshToken, Duration.ofDays(7));

                    Cookie newRefreshCookie = new Cookie("refreshToken", newRefreshToken);
                    newRefreshCookie.setHttpOnly(true);
                    newRefreshCookie.setSecure(true);
                    newRefreshCookie.setPath("/");
                    newRefreshCookie.setMaxAge(7 * 24 * 60 * 60);
                    response.addCookie(newRefreshCookie);

                    Cookie newAccessCookie = new Cookie("accessToken", newAccessToken);
                    newAccessCookie.setHttpOnly(true);
                    newAccessCookie.setSecure(true);
                    newAccessCookie.setPath("/");
                    newAccessCookie.setMaxAge(15 * 60);
                    response.addCookie(newAccessCookie);

                } else {
                    throw new JwtException("AccessToken expired and RefreshToken invalid");
                }
            } else {
                DecodedJWT decodedJWT = jwtTokenProvider.parseClaims(token);

                String userId = decodedJWT.getClaim("userId").asString();
                String role = decodedJWT.getClaim("roles").asString();
                String userName = decodedJWT.getClaim("userName").asString();
                String uid = decodedJWT.getClaim("uid").asString();

                User user = User.builder()
                        .userId(userId)
                        .userName(userName)
                        .uid(uid)
                        .role(Role.valueOf(role))
                        .build();

                PrincipalDetails principalDetails = new PrincipalDetails(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        principalDetails, null, principalDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            log.warn("[JwtAuthorizationFilter] 토큰 파싱 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header;
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return "Bearer " + cookie.getValue();
                }
            }
        }
        return null;
    }

    private String resolveRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}