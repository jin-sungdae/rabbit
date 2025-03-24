package com.user.server.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.security.SecurityConfig;
import com.user.server.user.dto.RequestUser;
import com.user.server.user.entity.Role;
import com.user.server.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 API - 정상 요청")
    void registerUser_Success() throws Exception {
        // Given
        RequestUser requestUser = new RequestUser();
        requestUser.setUserId("testUser");
        requestUser.setUserPassword("securePassword");
        requestUser.setUserName("테스트 유저");
        requestUser.setEmail("test@example.com");
        requestUser.setBirthday("1995-05-15");
        requestUser.setPhoneNumber("010-1234-5678");
        requestUser.setZipcode("12345");
        requestUser.setAddress1("서울시 강남구");
        requestUser.setAddress2("테헤란로 123");
        requestUser.setRole(Role.USER);
        requestUser.setJoinRoot("google");
        requestUser.setIsMailing("Y");
        requestUser.setIsSms("Y");

        // Mock 설정
        Mockito.doNothing().when(userService).createUser(Mockito.any(RequestUser.class));

        // When & Then (회원가입 API 요청)
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser))
                        .with(csrf())

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("true"));
    }

    @Test
    @DisplayName("회원가입 API - 유효성 검사 실패 (잘못된 이메일)")
    void registerUser_InvalidEmail() throws Exception {
        // Given
        RequestUser requestUser = new RequestUser();
        requestUser.setUserId("testUser");
        requestUser.setUserPassword("securePassword");
        requestUser.setUserName("테스트 유저");
        requestUser.setEmail("invalid-email");
        requestUser.setBirthday("1995-05-15");
        requestUser.setPhoneNumber("010-1234-5678");
        requestUser.setZipcode("12345");
        requestUser.setAddress1("서울시 강남구");
        requestUser.setAddress2("테헤란로 123");
        requestUser.setRole(Role.USER);
        requestUser.setJoinRoot("google");
        requestUser.setIsMailing("Y");
        requestUser.setIsSms("Y");

        // When & Then
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isBadRequest());
    }
}