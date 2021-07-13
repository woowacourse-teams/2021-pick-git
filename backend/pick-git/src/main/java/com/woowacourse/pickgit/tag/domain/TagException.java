package com.woowacourse.pickgit.tag.domain;

public class TagException extends RuntimeException {

    private final int statusCode;

    public TagException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
