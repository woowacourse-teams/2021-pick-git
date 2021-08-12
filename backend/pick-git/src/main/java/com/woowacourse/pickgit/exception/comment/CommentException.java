package com.woowacourse.pickgit.exception.comment;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class CommentException extends ApplicationException {

    protected CommentException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
