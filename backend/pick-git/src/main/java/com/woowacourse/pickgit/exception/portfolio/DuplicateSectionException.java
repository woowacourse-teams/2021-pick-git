package com.woowacourse.pickgit.exception.portfolio;

import org.springframework.http.HttpStatus;

public class DuplicateSectionException extends PortfolioException {

    private static final String ERROR_CODE = "R0005";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "섹션 이름 중복 에러";

    public DuplicateSectionException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private DuplicateSectionException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
