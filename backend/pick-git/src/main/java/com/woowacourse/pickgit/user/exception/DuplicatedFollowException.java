package com.woowacourse.pickgit.user.exception;

public class DuplicatedFollowException extends PickgitException {

    private static final String MESSAGE = "이미 팔로우 중 입니다.";

    public DuplicatedFollowException() {
        super(MESSAGE, "U0002");
    }
}
