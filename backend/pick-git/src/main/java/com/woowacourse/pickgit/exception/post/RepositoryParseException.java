package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class RepositoryParseException extends PostException {

    public RepositoryParseException(
        String errorCode, HttpStatus httpStatus, String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
