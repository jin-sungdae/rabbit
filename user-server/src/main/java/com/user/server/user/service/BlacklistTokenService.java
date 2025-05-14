package com.user.server.user.service;

import com.user.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

    /**
     * Access Token을 블랙리스트에 등록
     * @param accessToken         블랙리스트에 넣을 Access Token
     * @param remainingExpiration 남은 만료시간 (밀리초). 이 시간만큼 Redis에 저장
     */
    public void addToBlacklist(String accessToken, long remainingExpiration) {
        if (remainingExpiration <= 0) {
            // 이미 만료된 토큰은 굳이 블랙리스트 저장할 필요 없음
            log.info("AccessToken already expired. Not adding to blacklist.");
            return;
        }

        String jti = jwtTokenProvider.getJti(accessToken);
        if (jti == null || jti.isBlank()) {
            log.warn("[Blacklist] Failed to extract jti from token");
            return;
        }


        String key = BLACKLIST_KEY_PREFIX + jti;

        // 남은 만료 시간 동안 "blacklisted"라는 값을 저장
        redisTemplate.opsForValue().set(key, "blacklisted", remainingExpiration, TimeUnit.MILLISECONDS);

        log.info("[Blacklist] Add token to blacklist - key: {}, TTL: {} ms", key, remainingExpiration);
    }

    /**
     * 블랙리스트 여부 확인
     */
    public boolean isBlacklisted(String jti) {
        String key = BLACKLIST_KEY_PREFIX + jti;
        return redisTemplate.hasKey(key);
    }
}

