package com.user.server.user.service;

import com.user.server.user.dto.RequestUser;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;


import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.PropertySource;

import org.springframework.test.context.ActiveProfiles;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:datasource.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServiceTest {


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("회원가입 성공 시 UserRepository가 정상적으로 호출되는지 테스트")
    void createUser_Success() {
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

        // When
        userService.createUser(requestUser);

        // Then
        User savedUser = userRepository.findByUserId("testUser").orElse(null);
        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUserId());
    }


    @Test
    @DisplayName("회원가입 시 예외가 발생해도 정상적으로 처리되는지 테스트")
    void createUser_ExceptionHandled() {
        // Given
        RequestUser requestUser = new RequestUser();
        requestUser.setUserId("testUser");
        requestUser.setUserPassword("securePassword");

        // Mocking
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB 오류"));

        // When & Then
        assertDoesNotThrow(() -> userService.createUser(requestUser));
    }
}