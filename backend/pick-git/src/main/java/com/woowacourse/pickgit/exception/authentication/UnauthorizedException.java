package com.woowacourse.pickgit.exception.authentication;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AuthenticationException {

    private static final String CODE = "A0002";
    private static final String MESSAGE = "κΆν μλ¬";

    public UnauthorizedException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
