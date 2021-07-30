package com.woowacourse.pickgit.exception.user;

import org.springframework.http.HttpStatus;

public class ContributionParseException extends UserException {

    public ContributionParseException(
        String errorCode, HttpStatus httpStatus, String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
