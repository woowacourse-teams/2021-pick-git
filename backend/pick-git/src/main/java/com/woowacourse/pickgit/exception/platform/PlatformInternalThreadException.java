package com.woowacourse.pickgit.exception.platform;

import org.springframework.http.HttpStatus;

public class PlatformInternalThreadException extends PlatformException{

    private static final String CODE = "V0003";
    private static final String MESSAGE = "외부 플랫폼 데이터 추출중 내부 스레드 오류";

    public PlatformInternalThreadException() {
        this(MESSAGE);
    }

    private PlatformInternalThreadException(String message) {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
