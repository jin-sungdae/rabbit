package com.user.server.user.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class RequestNotification {

    private String userId;
    private String notificationType;
    private Map<String, String> parameters;

}
