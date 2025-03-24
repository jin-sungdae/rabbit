package com.user.server.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisLoginFailRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final String PREFIX = "login:fail:";
    private final String LOCK_PREFIX = "login:lock:";
    private final String PERMANENT_LOCK_PREFIX = "login:permlock:";

    private String key(String userId) {
        return PREFIX + userId;
    }

    private String lockKey(String userId) {
        return LOCK_PREFIX + userId;
    }

    private String permanentLockKey(String userId) {
        return PERMANENT_LOCK_PREFIX + userId;
    }


    public void increaseFailCount(String userId) {
        String failKey = key(userId);
        Long failCount = redisTemplate.opsForValue().increment(failKey);

        if (failCount == 1) {
            redisTemplate.expire(failKey, Duration.ofDays(30)); // 카운트는 최대 30일 보존
        }

        // 지수 증가: 10초 * 3^(failCount - 1)
        long seconds = Math.min((long) (10 * Math.pow(3, failCount - 1)), 30L * 24 * 60 * 60);

        redisTemplate.opsForValue().set(lockKey(userId), "LOCKED", seconds, TimeUnit.SECONDS);

        if (seconds >= 30L * 24 * 60 * 60) {
            redisTemplate.opsForValue().set(permanentLockKey(userId), "LOCKED");
        }
    }


    public int getFailCount(String userId) {
        Object value = redisTemplate.opsForValue().get(key(userId));

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    public void clearFailCount(String userId) {
        if (!isPermanentlyLocked(userId)) {
            redisTemplate.delete(key(userId));
            redisTemplate.delete(lockKey(userId));
        }
    }


    public boolean isLocked(String userId) {
        return isPermanentlyLocked(userId) || redisTemplate.hasKey(lockKey(userId)) == Boolean.TRUE;
    }


    public boolean isPermanentlyLocked(String userId) {
        return redisTemplate.hasKey(permanentLockKey(userId)) != null &&
                Boolean.TRUE.equals(redisTemplate.hasKey(permanentLockKey(userId)));
    }

    public void clearLock(String userId) {
        redisTemplate.delete(lockKey(userId));
        redisTemplate.delete(permanentLockKey(userId));
    }

    public void forcePermanentLock(String userId) {
        redisTemplate.opsForValue().set(permanentLockKey(userId), "LOCKED");
    }
}