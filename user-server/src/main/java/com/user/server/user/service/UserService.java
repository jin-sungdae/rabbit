package com.user.server.user.service;

import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.redis.RedisUserSettingsRepository;
import com.user.server.user.dto.RequestNotification;
import com.user.server.user.dto.RequestUser;
import com.user.server.user.dto.UserSettings;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RedisUserSettingsRepository redisUserSettingsRepository;
    private final WebClient webClient;

    @Transactional
    public void createUser(RequestUser requestUser) {
        try {
            User user = requestUser.toEntity();
            userRepository.save(user);
            log.info("회원가입 - 저장된 User: {}", user);
            log.info("회원가입 - 저장된 User ID: {}", user.getUserId());


            if (user == null) {
                throw new RuntimeException("User 저장 실패: savedUser가 null입니다.");
            }

            log.info("회원가입 성공: User ID = {}", user.getUserId());

            saveDefaultUserSettings(user.getUserId());
            sendWelcomeNotification(requestUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("회원가입 중 오류 발생", e);
        }
    }

    void saveDefaultUserSettings(String userId) {
        UserSettings defaultSettings = UserSettings.builder()
                .isPush("Y")
                .isSms("Y")
                .isEmail("Y")
                .language("kr")
                .build();

        redisUserSettingsRepository.saveUserSettings(userId, defaultSettings);
    }


    public void sendWelcomeNotification(RequestUser requestUser) {
        RequestNotification request = RequestNotification.builder()
                .userId(String.valueOf(requestUser.getIndex()))
                .notificationType("EMAIL")
                .parameters(Map.of("username", requestUser.getUserName(), "email", requestUser.getEmail()))
                .build();

        webClient.post()
                .uri("/notifications/send")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("웰컴 이메일 요청 성공: {}", response))
                .doOnError(error -> log.error(" 웰컴 이메일 요청 실패: {}", error.getMessage()))
                .subscribe();
    }

    public User getUserById(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }
}
