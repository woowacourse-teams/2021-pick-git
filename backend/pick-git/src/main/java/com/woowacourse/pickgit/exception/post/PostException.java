package com.woowacourse.pickgit.exception.post;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class PostException extends ApplicationException {

    protected PostException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
