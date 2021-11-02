package com.woowacourse.pickgit.exception.portfolio;

import org.springframework.http.HttpStatus;

public class InvalidProjectDateException extends PortfolioException {

    private static final String ERROR_CODE = "R0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "프로젝트 시작 날짜 > 종료 날짜 에러";

    public InvalidProjectDateException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private InvalidProjectDateException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
