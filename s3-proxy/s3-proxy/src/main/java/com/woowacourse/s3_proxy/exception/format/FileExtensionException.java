package com.woowacourse.s3_proxy.exception.format;

import com.woowacourse.s3_proxy.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FileExtensionException extends ApplicationException {

    private static final String ERROR_CODE = "I0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public FileExtensionException(String message) {
        super(ERROR_CODE, HTTP_STATUS, message);
    }
}
