package com.user.server.user.repository;

import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserSaveAndFind() {
        // Given
        User user = User.builder()
                .userId("sjin")
                .uid("asdfasdf")
                .userPassword("adfasf")
                .userName("진성대")
                .role(Role.USER)
                .phoneNo("010-1234-1234")
                .birthday("2000-01-01")
                .zipcode("adfasdf")
                .address1("adfa")
                .address2("adfadfa")
                .email("test@test.com")
                .joinRoot("job")
                .isSms(true)
                .isMailing(false)
                .regDate(LocalDateTime.now())
                .build();


        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo("sjin");
    }
}