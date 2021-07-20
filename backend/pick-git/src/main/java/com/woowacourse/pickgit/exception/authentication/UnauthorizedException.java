package com.woowacourse.pickgit.exception.authentication;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AuthenticationException {

    private static final String CODE = "A0002";
    private static final String MESSAGE = "권한 에러";

    public UnauthorizedException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
