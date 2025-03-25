package com.user.server.user.service;

import com.common.config.api.exception.GeneralException;
import com.user.server.user.dto.CustomUserPrincipal;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User;
        try {
            oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        } catch (OAuth2AuthenticationException ex) {
            log.error("카카오 사용자 정보 요청 실패: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new GeneralException("카카오 사용자 정보 요청 중 예외 발생", e);
        }

        if (oauth2User == null) {
            throw new GeneralException("카카오 사용자 정보가 null입니다.");
        }

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email;
        String nickname;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");

        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
        } else {
            nickname = "";
            email = "";
            throw new OAuth2AuthenticationException("Unsupported OAuth Provider: " + registrationId);
        }

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not provided by " + registrationId);
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .uid(RandomStringUtils.randomAlphanumeric(16))
                        .email(email)
                        .userName(nickname != null ? nickname : "NONAME")
                        .userId(RandomStringUtils.randomAlphanumeric(16) + registrationId.toUpperCase())
                        .userPassword(UUID.randomUUID().toString())
                        .birthday("1990-01-01")
                        .phoneNo("010-0000-0000")
                        .role(Role.USER)
                        .joinRoot(registrationId.toUpperCase())
                        .build()));

        return new CustomUserPrincipal(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes,
                userNameAttributeName,
                user
        );
    }
}
