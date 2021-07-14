package com.woowacourse.pickgit.exception.authentication;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class AuthenticationException extends ApplicationException {

    public AuthenticationException(String errorCode, HttpStatus httpStatus) {
        super(errorCode, httpStatus);
    }
}
