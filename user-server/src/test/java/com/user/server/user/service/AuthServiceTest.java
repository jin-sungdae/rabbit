package com.user.server.user.service;

import com.common.config.api.exception.GeneralException;

import com.user.config.security.JwtTokenProvider;
import com.user.server.UserApplication;
import com.user.server.redis.RedisLoginFailRepository;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UserApplication.class})
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private AuthService authService;

    @Autowired
    private RedisLoginFailRepository redisLoginFailRepository;

    private final String userId = "testUser";
    private final String wrongPassword = "wrong_password";
    private final String correctPassword = "correct_password";



    @BeforeEach
    void setUp() {
        redisLoginFailRepository.clearFailCount(userId);

        User user = User.builder()
                .userId("testUser")
                .uid("adfas")
                .userPassword(correctPassword)
                .email("asdf@adfa")
                .userName("테스터")
                .role(Role.USER)
                .birthday("1990-01-01")
                .phoneNo("010-123-1231")
                .build();

        userRepository.save(user);
        userRepository.flush();
    }

    @AfterEach
    void tearDown() {
        redisLoginFailRepository.clearFailCount(userId);
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("성공 로그인 시 실패 카운트 초기화")
    void loginSuccessShouldResetFailCount() {
        authService.login(userId, correctPassword);
        assertEquals(0, redisLoginFailRepository.getFailCount(userId));
    }

    @Test
    @DisplayName("연속 실패 시 failCount 증가")
    void testIncrementFailCount() throws InterruptedException {

        for (int i = 1; i <= 3; i++) {
            try {
                authService.login(userId, wrongPassword);
            } catch (GeneralException ignored) {}
            long seconds = Math.min((long) (10 * Math.pow(3, i - 1)), 30L * 24 * 60 * 60);
            Thread.sleep(seconds * 1000);
        }

        int failCount = redisLoginFailRepository.getFailCount(userId);
        assertEquals(3, failCount);
    }

    @Test
    @DisplayName("3회 시도 후 로그인 성공 시 failCount 초기화")
    void testLoginResetsFailCountAfterSuccess() throws InterruptedException {
        for (int i = 1; i <= 3; i++) {
            try {
                authService.login(userId, wrongPassword);
            } catch (GeneralException ignored) {}
            long seconds = Math.min((long) (10 * Math.pow(3, i - 1)), 30L * 24 * 60 * 60);
            Thread.sleep(seconds * 1000);
        }

        authService.login(userId, correctPassword);

        int failCount = redisLoginFailRepository.getFailCount(userId);
        assertEquals(0, failCount);
    }

    @Test
    @DisplayName("로그인 실패 시 지수 증가 방식으로 잠금 시간 증가")
    void shouldLockExponentially() {
        for (int i = 1; i <= 5; i++) {
            try {
                authService.login(userId, wrongPassword);
            } catch (GeneralException ignored) {}
            assertTrue(redisLoginFailRepository.isLocked(userId));
        }
    }

    @Test
    @DisplayName("정상 로그인 시 failCount와 lock 해제 (영구 차단 제외)")
    void shouldClearFailCountOnSuccess() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try {
                authService.login(userId, wrongPassword);
            } catch (GeneralException ignored) {}
            long seconds = Math.min((long) (10 * Math.pow(3, i - 1)), 30L * 24 * 60 * 60);
            Thread.sleep(seconds * 1000);
        }

        authService.login(userId, correctPassword);

        assertEquals(0, redisLoginFailRepository.getFailCount(userId));
        assertFalse(redisLoginFailRepository.isLocked(userId));
    }

}