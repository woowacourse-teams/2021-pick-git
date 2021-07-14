package com.woowacourse.pickgit.exception;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

    private String errorCode;
    private HttpStatus httpStatus;

    public ApplicationException(String errorCode, HttpStatus httpStatus) {
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
