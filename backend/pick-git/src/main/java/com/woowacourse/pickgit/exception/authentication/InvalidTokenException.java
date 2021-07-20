package com.woowacourse.pickgit.exception.authentication;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthenticationException {

    private static final String CODE = "A0001";
    private static final String MESSAGE = "토큰 인증 에러";

    public InvalidTokenException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
