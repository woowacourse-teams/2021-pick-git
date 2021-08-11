package com.woowacourse.pickgit.exception.comment;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends CommentException {

    private static final String CODE = "P0008";
    private static final String MESSAGE = "해당 댓글이 존재하지 않습니다.";

    public CommentNotFoundException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
