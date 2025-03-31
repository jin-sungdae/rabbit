package com.user.server.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthorizationFilterIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${secret.key}")
    private String secretKey;

    private static final String TEST_USER_ID = "testUser";

    @Test
    @DisplayName("토큰 없이 호출 → 200이 아니라 401 또는 403 예상")
    public void testApiWithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/user/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰 넣고 호출 200 예상")
    public void testApiWithValidToken_ShouldReturnOk() throws Exception {

        String token = JWT.create()
                .withClaim("userId", TEST_USER_ID)
                .sign(Algorithm.HMAC512(secretKey));


        mockMvc.perform(get("/api/v1/user/test")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("true")));
    }

    @Test
    @DisplayName("유효하지 않은 토큰 -> 401 Unauthorized")
    void invalidTokenShouldReturn401() throws Exception {
        // given
        String invalidToken = createInvalidSignatureToken("testUser");

        // when & then
        mockMvc.perform(get("/api/v1/user/test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("만료된 토큰 -> 401 Unauthorized")
    void expiredTokenShouldReturn401() throws Exception {
        // given
        String expiredToken = createExpiredToken("testUser");

        // when & then
        mockMvc.perform(get("/api/v1/user/test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }



    private String createInvalidSignatureToken(String userId) {
        long now = System.currentTimeMillis();
        long future = now + (5 * 60 * 1000);
        String wrongKey = "qwertyuiopasdfghjklzxcvbnm123456";

        return JWT.create()
                .withSubject(userId)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(future))
                .sign(Algorithm.HMAC512(wrongKey));
    }

    private String createExpiredToken(String userId) {
        long now = System.currentTimeMillis();
        long exp = now - 1000;

        return JWT.create()
                .withSubject(userId)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(exp))
                .sign(Algorithm.HMAC512(secretKey));
    }
}
