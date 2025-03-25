package com.user.server.user.dto;

import com.user.server.user.entity.User;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class RequestUserProfile {
    private String userName;
    private String email;
    private String phoneNo;
    private String birthday;
    private String zipcode;
    private String address1;
    private String address2;
    private boolean isMailing;
    private boolean isSms;

}
