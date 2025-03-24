package com.common.config.api.apidto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class APIDataResponse<T>  extends APIErrorResponse{

    private final T data;

    protected APIDataResponse(T data) {
        super(HttpStatus.OK.value());
        this.data = data;
    }

    private APIDataResponse(T data, String message) {
        super(HttpStatus.OK.value());
        this.data = data;
    }

    public static <T> APIDataResponse<T> of (T data) {
        return new APIDataResponse<>(data);
    }

    public static <T> APIDataResponse<T> of (T data, String message) {
        return new APIDataResponse<>(data, message);
    }

    public static <T> APIDataResponse<T> empty () {
        return new APIDataResponse<>(null);
    }
}