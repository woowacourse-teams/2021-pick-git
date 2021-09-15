package com.woowacourse.pickgit.exception.authentication;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends AuthenticationException {

    private static final String CODE = "A0003";
    private static final String MESSAGE = "리프레쉬 토큰 인증 에러";

    public InvalidRefreshTokenException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
