package com.woowacourse.pickgit.post.domain;

public class CannotAddTagException extends PostException {

    public CannotAddTagException() {
        super("P0001", 400);
    }
}
