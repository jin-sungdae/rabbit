package com.common.config.api.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    OK(0, ErrorCategory.NORMAL, "OK"),
    BAD_REQUEST(10000, ErrorCategory.CLIENT_SIDE, "bad request"),
    SPRING_BAD_REQUEST(10001, ErrorCategory.CLIENT_SIDE, "Spring-detected bad request"),
    VALIDATION_ERROR(10002, ErrorCategory.CLIENT_SIDE, "Validation error"),
    ARG_ERROR(10003, ErrorCategory.CLIENT_SIDE, "Failed to convert value of type"),
    NOT_FOUND(10003, ErrorCategory.CLIENT_SIDE, "Requested resource is not found"),

    HTTP_CONFLICT(10009, ErrorCategory.CLIENT_SIDE, "The requested operation could not be completed due to a conflict with the current state of the resource."),

    INTERNAL_ERROR(20000, ErrorCategory.SERVER_SIDE, "internal error"),
    SPRING_INTERNAL_ERROR(200001, ErrorCategory.SERVER_SIDE, "Spring-detected internal error"),

    DATA_ACCESS_ERROR(20002, ErrorCategory.SERVER_SIDE, "Data access error"),
    DATA_DUPLICATION_ERROR(20003, ErrorCategory.SERVER_SIDE, "Data duplication error"),
    DATA_API_ERROR(20004, ErrorCategory.SERVER_SIDE, "external API error"),
    NOT_FOUND_PRODUCT(20005, ErrorCategory.SERVER_SIDE , "not found Production" ),
    FORBIDDEN(20006, ErrorCategory.SERVER_SIDE, "forbidden access"),;


    private final Integer code;
    private final ErrorCategory errorCategory;
    private final String message;

    public String getMessage(Exception e) {
        return getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(getMessage());
    }

    public boolean isClientSideError() {
        return this.getErrorCategory() == ErrorCategory.CLIENT_SIDE;
    }

    public boolean isServerSideError() {
        return this.getErrorCategory() == ErrorCategory.SERVER_SIDE;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name(), this.getCode());
    }

    public enum ErrorCategory{
        NORMAL, CLIENT_SIDE, SERVER_SIDE
    }
}
