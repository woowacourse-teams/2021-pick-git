package com.woowacourse.s3_proxy.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public ApplicationException(String errorCode, HttpStatus httpStatus, String message) {
        this(errorCode, httpStatus, message, null);
    }

    public ApplicationException(
        String errorCode,
        HttpStatus httpStatus,
        String message,
        Exception e
    ) {
        super(message, e);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
