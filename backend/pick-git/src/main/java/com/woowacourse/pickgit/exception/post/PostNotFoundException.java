package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends PostException {

    private static final String ERROR_CODE = "P0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final String MESSAGE = "해당하는 게시물을 찾을 수 없습니다.";

    public PostNotFoundException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    public PostNotFoundException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
