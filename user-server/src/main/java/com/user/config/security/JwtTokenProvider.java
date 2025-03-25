package com.user.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.user.server.user.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@RequiredArgsConstructor
//@Profile("!test")
public class JwtTokenProvider {

//    @Value("${jwt.secret}")
    private String secretKey = "123";

    // 예: 15분
    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000L;
    // 예: 7일
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

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
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .sign(algorithm);
    }

    public String createRefreshToken(User user) {
        return createRefreshToken(user.getUserId());
    }

    public String createRefreshToken(String userId) {
        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
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
}
