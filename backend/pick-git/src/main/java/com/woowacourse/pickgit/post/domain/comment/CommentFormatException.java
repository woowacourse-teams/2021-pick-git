package com.woowacourse.pickgit.post.domain.comment;

public class CommentFormatException extends CommentException {

    public CommentFormatException() {
        super("F0002", 400);
    }
}
