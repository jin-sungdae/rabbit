package com.common.config.api.exception;

import com.common.config.api.constant.ErrorCode;

public class ForbiddenAccessException extends GeneralException {
    public ForbiddenAccessException() {
        super(ErrorCode.FORBIDDEN, "해당 상품이 존재하지 않습니다.");
    }

    public ForbiddenAccessException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
