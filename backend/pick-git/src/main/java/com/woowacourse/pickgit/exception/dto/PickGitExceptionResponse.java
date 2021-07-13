package com.woowacourse.pickgit.exception.dto;

public class PickGitExceptionResponse {

    private String errorCode;

    private PickGitExceptionResponse() {
    }

    public PickGitExceptionResponse(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
