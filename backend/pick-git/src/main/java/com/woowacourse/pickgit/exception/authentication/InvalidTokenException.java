package com.woowacourse.pickgit.exception.authentication;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthenticationException {

    private static final String errorCode = "A0001";

    public InvalidTokenException() {
        super(errorCode, HttpStatus.UNAUTHORIZED, "토큰 인증 에러");
    }
}
