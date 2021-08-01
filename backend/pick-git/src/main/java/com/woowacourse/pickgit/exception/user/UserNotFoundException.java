package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException {

    private static final String CODE = "U0001";
    private static final String MESSAGE = "해당하는 사용자를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }
}
