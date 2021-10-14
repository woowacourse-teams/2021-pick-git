package com.woowacourse.pickgit.exception.redis;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class EmbeddedRedisServerException extends ApplicationException {

    private static final String ERROR_CODE = "V0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final String MESSAGE = "Redis 내장 서버 연동 에러";

    public EmbeddedRedisServerException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    private EmbeddedRedisServerException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
