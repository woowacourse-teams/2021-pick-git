package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class CommentFormatException extends PostException {

    private static final String CODE = "F0002";
    private static final String MESSAGE = "댓글 길이 에러";

    public CommentFormatException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
