package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class TagFormatException extends PostException {

    private static final String CODE = "F0003";
    private static final String MESSAGE = "태그 포맷 에러";

    public TagFormatException() {
        super(CODE, HttpStatus.BAD_REQUEST,MESSAGE);
    }
}
