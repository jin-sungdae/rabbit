package com.common.config.api.exception;

import com.common.config.api.constant.ErrorCode;

public class ProductNotFoundException extends GeneralException{

    public ProductNotFoundException() {
        super(ErrorCode.NOT_FOUND_PRODUCT, "해당 상품이 존재하지 않습니다.");
    }

    public ProductNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND_PRODUCT, message);
    }
}
