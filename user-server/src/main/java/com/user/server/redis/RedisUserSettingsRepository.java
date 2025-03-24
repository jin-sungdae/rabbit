package com.user.server.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.server.user.dto.UserSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisUserSettingsRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String USER_SETTINGS_KEY = "user:%s:settings";

    /**
     * 사용자의 알림 설정을 Redis에 저장
     */
    public void saveUserSettings(String userId, UserSettings settings) {
        String key = String.format(USER_SETTINGS_KEY, userId);
        try {
            String value = objectMapper.writeValueAsString(settings);
            redisTemplate.opsForValue().set(key, value);
            log.info("Redis에 사용자 알림 설정 저장: {}", key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 오류", e);
        }
    }
}
