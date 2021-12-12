package com.woowacourse.pickgit.exception.upload.upload;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UploadFailureException extends ApplicationException {

    private static final String ERROR_CODE = "I0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "업로드 실패";

    public UploadFailureException() {
        super(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }
}
