package com.user.server.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.server.UserApplication;
import com.user.server.support.security.WithMockPrincipal;
import com.user.server.user.dto.RequestUserProfile;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {UserApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeAll
    void setUp() {
        testUser = User.builder()
                .userId("testuser")
                .uid("uid-test-123")
                .userPassword("encoded-password")
                .userName("테스트 사용자")
                .role(Role.USER)
                .email("test@example.com")
                .phoneNo("01012345678")
                .zipcode("12345")
                .address1("서울시 강남구")
                .address2("테스트빌딩")
                .birthday("1990-01-01")
                .isMailing(true)
                .isSms(true)
                .build();

        userRepository.save(testUser);
        userRepository.flush();
    }

    @Test
    @DisplayName("프로필 조회 API - 정상 요청")
    @WithMockPrincipal(uid = "uid-test-123")
    void getUserProfile_shouldReturnUserData() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("테스트 사용자"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }


    @Test
    @DisplayName("프로필 수정 API - 정상 요청")
    @WithMockPrincipal(uid = "uid-test-123")
    void updateUserProfile_shouldUpdateAndReturnSuccess() throws Exception {
        RequestUserProfile updateDto = new RequestUserProfile();
        updateDto.setUserName("수정된 사용자");
        updateDto.setEmail("new@example.com");
        updateDto.setPhoneNo("01099998888");
        updateDto.setBirthday("1992-02-02");
        updateDto.setZipcode("67890");
        updateDto.setAddress1("서울시 마포구");
        updateDto.setAddress2("수정된 건물");
        updateDto.setMailing(false);
        updateDto.setSms(false);

        mockMvc.perform(put("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("true"));
    }


    /**
     * 테스트 목적
     * 1.	정상 요청이 통과되는지
     * 2.	요청이 초과되었을 때 fallback 메서드가 호출되는지
     * 3.	요청 수 제한 설정(limitForPeriod, limitRefreshPeriod)이 실제로 적용되는지
     * @throws Exception
     */
    @Test
    @DisplayName("RateLimiter - 유저 프로필 수정 제한 테스트")
    @WithMockPrincipal(uid = "uid-test-123")
    void rateLimitExceeded_shouldTriggerFallback() throws Exception {
        RequestUserProfile updateDto = new RequestUserProfile();
        updateDto.setUserName("유저");
        updateDto.setEmail("a@a.com");

        String body = objectMapper.writeValueAsString(updateDto);

        // 1. 첫 요청 - 허용
        mockMvc.perform(put("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("true"));

        // 2. 두 번째 요청 - 허용
        mockMvc.perform(put("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        // 3. 세 번째 요청 - 제한 초과
        mockMvc.perform(put("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("요청이 너무 많습니다. 잠시 후 다시 시도하세요."))
                .andExpect(jsonPath("$.data").value("false"));
    }
}