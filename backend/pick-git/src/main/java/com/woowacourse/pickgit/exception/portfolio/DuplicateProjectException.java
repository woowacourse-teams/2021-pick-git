package com.woowacourse.pickgit.exception.portfolio;

import org.springframework.http.HttpStatus;

public class DuplicateProjectException extends PortfolioException {

    private static final String ERROR_CODE = "R0004";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "프로젝트 이름 중복 에러";

    public DuplicateProjectException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private DuplicateProjectException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
