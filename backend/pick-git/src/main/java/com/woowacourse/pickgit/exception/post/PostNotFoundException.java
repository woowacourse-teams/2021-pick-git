package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends PostException {

    public PostNotFoundException(String errorCode, HttpStatus httpStatus,
        String message) {
        super(errorCode, httpStatus, message);
    }
}
