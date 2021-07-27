package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class RepositoryParseException extends PostException {
    private static final String ERROR_CODE = "V0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "레포지토리 목록을 불러올 수 없습니다.";

    public RepositoryParseException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    public RepositoryParseException(
        String errorCode, HttpStatus httpStatus, String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
