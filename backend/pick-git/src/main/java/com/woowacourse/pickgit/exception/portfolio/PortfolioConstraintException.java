package com.woowacourse.pickgit.exception.portfolio;

import org.springframework.http.HttpStatus;

public class PortfolioConstraintException extends PortfolioException {

    private static final String ERROR_CODE = "R0006";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public PortfolioConstraintException(String message) {
        this(ERROR_CODE, HTTP_STATUS, message);
    }

    protected PortfolioConstraintException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
