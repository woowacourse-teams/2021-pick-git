package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class CannotAddTagException extends PostException {

    private static final String CODE = "P0001";
    private static final String MESSAGE = "태그 추가 에러";

    public CannotAddTagException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
