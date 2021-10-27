package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class PostSearchTypeException extends PostException {

    private static final String ERROR_CODE = "P0010";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final String MESSAGE = "포스트 검색 타입을 찾을 수 없습니다.";

    public PostSearchTypeException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    protected PostSearchTypeException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
