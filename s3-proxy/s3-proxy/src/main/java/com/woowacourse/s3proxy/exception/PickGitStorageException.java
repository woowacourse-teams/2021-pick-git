package com.woowacourse.s3proxy.exception;

public class PickGitStorageException extends RuntimeException {

    public PickGitStorageException() {
    }

    public PickGitStorageException(String message) {
        super(message);
    }

    public PickGitStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public PickGitStorageException(Throwable cause) {
        super(cause);
    }

    public PickGitStorageException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
