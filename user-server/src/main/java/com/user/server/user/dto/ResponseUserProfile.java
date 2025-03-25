package com.user.server.user.dto;

import com.user.server.user.entity.User;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseUserProfile {

    private String userName;
    private String email;
    private String phoneNo;
    private String birthday;
    private String zipcode;
    private String address1;
    private String address2;
    private boolean isMailing;
    private boolean isSms;

    public static ResponseUserProfile toDto(User user) {
        return ResponseUserProfile.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .birthday(user.getBirthday())
                .zipcode(user.getZipcode())
                .address1(user.getAddress1())
                .address2(user.getAddress2())
                .isMailing(user.isMailing())
                .isSms(user.isSms())
                .build();
    }
}
