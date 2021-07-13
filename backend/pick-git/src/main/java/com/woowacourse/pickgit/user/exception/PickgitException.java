package com.woowacourse.pickgit.user.exception;

public class PickgitException extends RuntimeException {

    protected String errorCode;

    public PickgitException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
