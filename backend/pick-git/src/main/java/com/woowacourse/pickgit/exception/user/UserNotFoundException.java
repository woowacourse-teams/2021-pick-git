package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String errorCode, HttpStatus httpStatus,
        String message) {
        super(errorCode, httpStatus, message);
    }
}
