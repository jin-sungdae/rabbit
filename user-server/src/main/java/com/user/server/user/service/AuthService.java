package com.user.server.user.service;

import com.common.config.api.exception.GeneralException;
import com.user.server.redis.RedisLoginFailRepository;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUserRefreshRepository redisUserRefreshRepository;
    private final RedisLoginFailRepository redisLoginFailRepository;
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);


    public void saveRefreshToken(String userId, String refreshToken) {
        redisUserRefreshRepository.saveRefreshToken(userId, refreshToken, REFRESH_TOKEN_EXPIRATION);
    }

    public User login(String userId, String password) {
        if (redisLoginFailRepository.isLocked(userId)) {
            throw new GeneralException("현재 로그인 차단 상태입니다. 잠시 후 다시 시도하세요.");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException("존재하지 않는 사용자"));

//        if (!passwordEncoder.matches(password, user.getUserPassword())) {
//            redisLoginFailRepository.increaseFailCount(userId);
//            throw new GeneralException("비밀번호가 일치하지 않습니다.");
//        }

        if (!password.equals(user.getUserPassword())) {
            redisLoginFailRepository.increaseFailCount(userId);
            throw new GeneralException("비밀번호가 일치하지 않습니다.");
        }

        if (!redisLoginFailRepository.isPermanentlyLocked(userId)) {
            redisLoginFailRepository.clearFailCount(userId);
        }

        return user;
    }
}
