package com.woowacourse.pickgit.exception.platform;

import org.springframework.http.HttpStatus;

public class PlatformHttpErrorException extends PlatformException {

    private static final String CODE = "V0001";
    private static final String MESSAGE = "외부 플랫폼 연동에 실패";

    public PlatformHttpErrorException() {
        this(MESSAGE);
    }

    public PlatformHttpErrorException(String message) {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
