package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class ContributionParseException extends UserException {

    private static final String CODE = "V0001";
    private static final String MESSAGE = "활동 통계를 조회할 수 없습니다.";

    public ContributionParseException() {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }
}
