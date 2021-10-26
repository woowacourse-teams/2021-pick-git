package com.woowacourse.pickgit.exception.portfolio;

import org.springframework.http.HttpStatus;

public class NoSuchPortfolioException extends PortfolioException {

    private static final String ERROR_CODE = "R0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "존재하지 않는 포트폴리오 조회 에러";

    public NoSuchPortfolioException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private NoSuchPortfolioException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
