package com.user.server.redis;

import com.common.config.api.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.server.user.dto.UserSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisUserRefreshRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REFRESH_TOKEN_KEY = "user:%s:refreshToken";


    public void saveRefreshToken(String userId, String refreshToken, long expiration) {
        String key = String.format(REFRESH_TOKEN_KEY, userId);

        // JSON 형태로 저장할 데이터 (RefreshToken + IP주소 등)
        RefreshTokenData refreshTokenData = new RefreshTokenData(refreshToken, System.currentTimeMillis());

        try {
            String value = objectMapper.writeValueAsString(refreshTokenData);
            redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
            log.info("Redis에 Refresh Token 저장: {}", key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 오류", e);
        }
    }

    public String getRefreshToken(String userId) throws JsonProcessingException {
        String key = String.format(REFRESH_TOKEN_KEY, userId);
        String value = redisTemplate.opsForValue().get(key);
        String saveRefreshToken = "";

        try {
            JsonNode jsonNode = objectMapper.readTree(value);
            saveRefreshToken = jsonNode.get("refreshToken").asText();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new GeneralException("Redis에서 추출한 Refresh Token을 파싱하는데 실패했습니다.");
        }

        return saveRefreshToken;

    }

    public void deleteRefreshToken(String userId) {
        String key = String.format(REFRESH_TOKEN_KEY, userId);
        redisTemplate.delete(key);
        log.info("Redis에서 Refresh Token 삭제: {}", key);
    }

    // Refresh Token 정보를 JSON으로 저장하기 위한 DTO
    private record RefreshTokenData(String refreshToken, long createdAt) {}

}
