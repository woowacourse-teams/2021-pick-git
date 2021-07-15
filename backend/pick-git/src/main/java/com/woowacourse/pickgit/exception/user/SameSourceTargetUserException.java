package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class SameSourceTargetUserException extends UserException {

    private static final String CODE = "U0004";
    private static final String MESSAGE = "같은 Source 와 Target 유저입니다.";

    public SameSourceTargetUserException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
