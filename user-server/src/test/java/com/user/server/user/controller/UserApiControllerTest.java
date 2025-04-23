package com.user.server.user.controller;

import com.common.config.api.controller.APIExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.security.JwtTokenProvider;
import com.user.config.security.SecurityConfig;
import com.user.server.redis.RedisUserRefreshRepository;
import com.user.server.user.dto.RequestUser;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.service.AuthService;
import com.user.server.user.service.BlacklistTokenService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserApiController.class)
@Import(APIExceptionHandler.class)
//@Import(SecurityConfig.class)
//@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisUserRefreshRepository redisUserRefreshRepository;

    @MockBean
    private BlacklistTokenService blacklistTokenService;

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
        Mockito.doNothing().when(userService).createUser(any(RequestUser.class));

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

    // TODO : 302 Error 해결
    @Test
    @DisplayName("로그인 성공 시 Access/Refresh/CSRF 쿠키 및 사용자 정보 반환")
    void login_success_returns_token_cookies_and_user_info() throws Exception {
        // given
        RequestUser request = RequestUser.builder()
                .userId("testuser")
                .userPassword("password123")
                .build();
        User user = User.builder()
                .id(1L)
                .userId("testuser")
                .userPassword("password123")
                .userName("진성대")
                .role(Role.USER)
                .build();

        given(authService.login(eq("testuser"), eq("password123"))).willReturn(user);
        given(jwtTokenProvider.createAccessToken(any())).willReturn("access.jwt.token");
        given(jwtTokenProvider.createRefreshToken((User) any())).willReturn("refresh.jwt.token");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()) )

                .andExpect(status().isOk())
                .andExpect(cookie().value("accessToken", "access.jwt.token"))
                .andExpect(cookie().value("refreshToken", "refresh.jwt.token"))
                .andExpect(cookie().value("csrfToken", "access.jwt.token"))
                .andExpect(jsonPath("$.data.userId").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.userName").value("진성대"));

        then(authService).should().saveRefreshToken(eq(user.getUserId()), eq("refresh.jwt.token"));
        then(userService).should().saveUserInfoByCache(eq(user.getUid()));
    }
}