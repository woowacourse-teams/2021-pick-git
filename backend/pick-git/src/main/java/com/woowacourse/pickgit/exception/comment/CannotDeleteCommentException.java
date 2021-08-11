package com.woowacourse.pickgit.exception.comment;

import org.springframework.http.HttpStatus;

public class CannotDeleteCommentException extends CommentException {

    private static final String CODE = "P0007";
    private static final String MESSAGE = "남 게시물, 남 댓글은 삭제할 수 없습니다.";

    public CannotDeleteCommentException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
