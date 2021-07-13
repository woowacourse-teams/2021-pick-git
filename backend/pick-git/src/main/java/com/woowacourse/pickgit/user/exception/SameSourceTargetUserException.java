package com.woowacourse.pickgit.user.exception;

public class SameSourceTargetUserException extends PickgitException {

    private static final String MESSAGE = "같은 Source 와 Target 유저입니다.";

    public SameSourceTargetUserException() {
        super(MESSAGE, "U0004");
    }
}
