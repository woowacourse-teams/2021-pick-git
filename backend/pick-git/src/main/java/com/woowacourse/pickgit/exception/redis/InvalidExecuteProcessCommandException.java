package com.woowacourse.pickgit.exception.redis;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class InvalidExecuteProcessCommandException extends ApplicationException {

    private static final String ERROR_CODE = "V0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final String MESSAGE = "Redis 내장 서버 Port 조회 코드 예외";

    public InvalidExecuteProcessCommandException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    public InvalidExecuteProcessCommandException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
