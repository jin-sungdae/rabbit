package com.common.config.api.apidto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class APIErrorResponse {

    private final int success;

    public static APIErrorResponse of (HttpStatus httpStatus) {
        return new APIErrorResponse(httpStatus.value());
    }

}
