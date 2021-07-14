package com.woowacourse.pickgit.exception.post;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class PostException extends ApplicationException {

    public PostException(String errorCode, HttpStatus httpStatus) {
        super(errorCode, httpStatus);
    }
}
