package com.woowacourse.pickgit.post.domain.comment;

public class CommentException extends RuntimeException {

    private final int statusCode = 400;

    public CommentException(String errorCode) {
        super(errorCode);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
