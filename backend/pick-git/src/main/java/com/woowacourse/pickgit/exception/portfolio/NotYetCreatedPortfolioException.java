package com.woowacourse.pickgit.exception.portfolio;

import org.springframework.http.HttpStatus;

public class NotYetCreatedPortfolioException extends PortfolioException {

    private static final String ERROR_CODE = "R0006";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NO_CONTENT;
    private static final String MESSAGE = "아직 생성되지 않은 포트폴리오 조회";

    public NotYetCreatedPortfolioException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private NotYetCreatedPortfolioException(String errorCode, HttpStatus httpStatus,
        String message) {
        super(errorCode, httpStatus, message);
    }
}
