package com.user.server.user.dto;

import com.user.server.user.entity.Role;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponseUser {
    private String userId;
    private String userName;
    private Role role;
}
