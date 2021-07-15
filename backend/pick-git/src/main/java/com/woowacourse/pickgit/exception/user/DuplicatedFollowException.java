package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class DuplicatedFollowException extends UserException {

    private static final String CODE = "U0002";
    private static final String MESSAGE = "이미 팔로우 중 입니다.";

    public DuplicatedFollowException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
