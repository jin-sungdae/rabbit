package com.user.server.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSettings {
    private String isPush;
    private String isSms;
    private String isEmail;
    private String language;

}
