package com.user.server.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.server.user.dto.ResponseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisUserInfoRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration TTL = Duration.ofMinutes(60);

    private String buildKey(String uid) {
        return "user:me:" + uid;
    }

    // 사용자 캐시 저장 (갱신 포함)
    public void saveUser(String uid, Object userObject) {
        try {
            String json = objectMapper.writeValueAsString(userObject);
            String key = buildKey(uid);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.info("[RedisUserInfoRepository] 캐시 저장 완료 - key: {}", key);
        } catch (JsonProcessingException e) {
            log.error("[RedisUserInfoRepository] 사용자 캐시 직렬화 실패: {}", e.getMessage());
        }
    }

    //사용자 캐시 조회
    public Optional<ResponseUser> getUserInfo(String uid) {
        String cached = redisTemplate.opsForValue().get(buildKey(uid));
        if (cached == null) return Optional.empty();

        try {
            return Optional.of(objectMapper.readValue(cached, ResponseUser.class));
        } catch (JsonProcessingException e) {
            return Optional.empty(); // 파싱 실패 시 무시
        }
    }

    // 사용자 캐시 무효화
    public void deleteUser(String uid) {
        String key = buildKey(uid);
        redisTemplate.delete(key);
        log.info("[RedisUserInfoRepository] 사용자 캐시 삭제 - key: {}", key);
    }
}
