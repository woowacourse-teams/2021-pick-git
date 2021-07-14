package com.woowacourse.pickgit.exception.platform;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class PlatformException extends ApplicationException {

    public PlatformException(String errorCode, HttpStatus httpStatus) {
        super(errorCode, httpStatus);
    }
}
