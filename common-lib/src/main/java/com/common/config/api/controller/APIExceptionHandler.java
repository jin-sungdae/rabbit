package com.common.config.api.controller;


import com.common.config.api.apidto.APIErrorResponse;
import com.common.config.api.exception.ForbiddenAccessException;
import com.common.config.api.exception.GeneralException;

import com.common.config.api.exception.ProductNotFoundException;
import com.sun.jdi.InternalException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class APIExceptionHandler extends ResponseEntityExceptionHandler {



    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> general(GeneralException e, WebRequest request) {
        HttpStatus status = e.getErrorCode().isClientSideError() ?
                HttpStatus.BAD_REQUEST :
                HttpStatus.INTERNAL_SERVER_ERROR;

        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> productNotFound(ProductNotFoundException e, WebRequest request) {
        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<Object> forbiddenAccess(ForbiddenAccessException e, WebRequest request) {
        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e, WebRequest request) {
        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception e, WebRequest webRequest) {
        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientError(HttpClientErrorException e, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(e.getRawStatusCode());
        if (status == HttpStatus.NOT_FOUND) {
            return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.NOT_FOUND, request);
        }

        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Object> handleHttpServerError(HttpServerErrorException e, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(e.getRawStatusCode());
        if (status == HttpStatus.NOT_FOUND) {
            return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.NOT_FOUND, request);
        }
        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<Object> handleInternalException(InternalException e, WebRequest webRequest) {
        return callSuperInternalExceptionHandler(e, HttpHeaders.EMPTY, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }


    private ResponseEntity<Object> callSuperInternalExceptionHandler(
            Exception e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest webRequest
    ) {
        APIErrorResponse errorResponse = APIErrorResponse.of(status);


        log.error("APIExceptionHandler==============", e);

        return new ResponseEntity<>(errorResponse, headers, status);
    }
}
