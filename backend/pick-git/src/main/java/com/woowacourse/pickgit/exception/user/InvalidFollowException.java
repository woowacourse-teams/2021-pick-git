package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class InvalidFollowException extends UserException {

    private static final String CODE = "U0003";
    private static final String MESSAGE = "존재하지 않는 팔로우 입니다.";

    public InvalidFollowException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
