package com.woowacourse.pickgit.user.exception;

public class InvalidFollowException extends PickgitException {

    private static final String MESSAGE = "존재하지 않는 팔로우 입니다.";

    public InvalidFollowException() {
        super(MESSAGE, "U0003");
    }
}
