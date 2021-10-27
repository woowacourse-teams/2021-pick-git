package com.woowacourse.pickgit.exception.portfolio;

import org.springframework.http.HttpStatus;

public class ProjectTypeNotFoundException extends PortfolioException {

    private static final String ERROR_CODE = "R0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "프로젝트 타입 찾을 수 없음 에러";

    public ProjectTypeNotFoundException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private ProjectTypeNotFoundException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
