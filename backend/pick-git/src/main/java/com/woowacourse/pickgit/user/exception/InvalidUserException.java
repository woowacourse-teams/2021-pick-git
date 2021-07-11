package com.woowacourse.pickgit.user.exception;

public class InvalidUserException extends PickgitException {

    private static final String MESSAGE = "유효하지 않은 유저입니다.";

    public InvalidUserException() {
        super(MESSAGE);
    }
}
