package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class InvalidUserException extends UserException {

    private static final String CODE = "U0001";
    private static final String MESSAGE = "유효하지 않은 유저입니다.";

    public InvalidUserException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
