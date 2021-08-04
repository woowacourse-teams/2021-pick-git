package com.woowacourse.s3_proxy.exception.format;

import com.woowacourse.s3_proxy.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class HashFailureException extends ApplicationException {

    private static final String ERROR_CODE = "I0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public HashFailureException(String message) {
        super(ERROR_CODE, HTTP_STATUS, message);
    }
}
