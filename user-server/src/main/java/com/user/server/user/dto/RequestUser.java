package com.user.server.user.dto;

import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;


import java.util.Random;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class RequestUser {
    private String index;
    private String userName;
    private String userId;
    private String userPassword;
    private Role role;
    private String zipcode;
    private String address1;
    private String address2;
    private String birthday;
    private String email;
    private String phoneNumber;
    private String address;
    private String joinRoot;
    private String isMailing;
    private String isSms;

    public User toEntity() {

        return User.builder()
                .userId(userId)
                .uid(RandomStringUtils.randomAlphanumeric(16))
                .userPassword(userPassword)
                .userName(userName)
                .zipcode(zipcode)
                .address1(address1)
                .address2(address2)
                .birthday(birthday)
                .email(email)
                .joinRoot(joinRoot)
                .phoneNo(phoneNumber)
                .role(role)
                .isSms(isSms.equals("true"))
                .isMailing(isMailing.equals("true"))
                .build();
    }
}
