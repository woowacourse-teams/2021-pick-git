package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class HomeFeedTypeException extends PostException {

    private static final String ERROR_CODE = "P0010";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final String MESSAGE = "홈피드 타입을 찾을 수 없습니다.";

    public HomeFeedTypeException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private HomeFeedTypeException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
