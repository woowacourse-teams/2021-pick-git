package com.woowacourse.s3_proxy.exception.upload;

import com.woowacourse.s3_proxy.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UploadFailureException extends ApplicationException {

    private static final String ERROR_CODE = "I0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "업로드 실패";

    public UploadFailureException() {
        super(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    public UploadFailureException(Exception e) {
        super(ERROR_CODE, HTTP_STATUS, MESSAGE, e);
    }
}
