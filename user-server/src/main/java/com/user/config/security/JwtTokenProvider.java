package com.user.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;


@Component
@RequiredArgsConstructor
@Slf4j
//@Profile("!test")
public class JwtTokenProvider {

//    @Value("${jwt.secret}")
    private String secretKey = "123";

    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);

    private final RedisUserRefreshRepository redisUserRefreshRepository;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC512(secretKey);
    }

    public String createAccessToken(User user) {
        return createAccessToken(user.getUserId(), user.getRole().name(), user.getUserName(), user.getUid());
    }

    public String createAccessToken(String userId, String role, String userName, String uid) {
        return JWT.create()
                .withSubject(userId)
                .withClaim("userId", userId)
                .withClaim("roles", role)
                .withClaim("userName", userName)
                .withClaim("uid", uid)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION.toMillis()))
                .sign(algorithm);
    }

    public String createRefreshToken(User user) {
        return createRefreshToken(user.getUserId());
    }

    public String createRefreshToken(String userId) {
        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION.toMillis()))
                .sign(algorithm);
    }

    public DecodedJWT parseClaims(String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    public long getRemainingExpiration(String token) {
        try {
            DecodedJWT decodedJWT = parseClaims(token);
            Date expiration = decodedJWT.getExpiresAt();
            long now = System.currentTimeMillis();
            return expiration.getTime() - now;
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean isExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);

            Date expiresAt = decodedJWT.getExpiresAt();
            return expiresAt.before(new Date());
        } catch (TokenExpiredException e) {
            return true;
        } catch (Exception e) {
            log.error("[JwtTokenProvider] 토큰 만료 검사 실패: {}", e.getMessage());
            return true;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(refreshToken);

            String userId = decodedJWT.getClaim("userId").asString();

            String storedToken = redisUserRefreshRepository.getRefreshToken(userId);
            return storedToken != null && storedToken.equals(refreshToken);

        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromRefreshToken(String refreshToken) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(refreshToken);
        return decodedJWT.getClaim("userId").asString();
    }


}
