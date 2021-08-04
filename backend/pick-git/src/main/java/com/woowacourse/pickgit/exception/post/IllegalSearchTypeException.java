package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class IllegalSearchTypeException extends PostException {

    private static final String ERROR_CODE = "P0006";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "검색 타입이 존재하지 않습니다.";

    public IllegalSearchTypeException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    public IllegalSearchTypeException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
