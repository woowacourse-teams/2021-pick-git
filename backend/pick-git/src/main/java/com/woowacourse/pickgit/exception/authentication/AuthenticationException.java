package com.woowacourse.pickgit.exception.authentication;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class AuthenticationException extends ApplicationException {

    protected AuthenticationException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
