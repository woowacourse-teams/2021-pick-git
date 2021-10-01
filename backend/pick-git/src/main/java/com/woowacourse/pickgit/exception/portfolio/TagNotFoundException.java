package com.woowacourse.pickgit.exception.portfolio;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class TagNotFoundException extends ApplicationException {

    private static final String ERROR_CODE = "R0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "태그를 찾을 수 없습니다.";

    public TagNotFoundException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private TagNotFoundException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }

}
