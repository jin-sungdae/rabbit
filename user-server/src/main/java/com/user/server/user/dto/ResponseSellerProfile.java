package com.user.server.user.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseSellerProfile {
    private String shopName;
    private String contact;
    private String description;
}
