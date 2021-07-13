package com.woowacourse.pickgit.post.domain;

public class PostException extends RuntimeException {

    private final int statusCode;

    public PostException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
