package com.woowacourse.pickgit.post.domain.comment;

public class CommentException extends RuntimeException {

    private final int statusCode;

    public CommentException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
