package com.woowacourse.s3proxy.exception;

public class UploadFailException extends PickGitStorageException {

    private static final String MESSAGE = "업로드 실패";

    public UploadFailException() {
        super(MESSAGE);
    }

    public UploadFailException(Exception e) {
        super(MESSAGE, e);
    }
}
