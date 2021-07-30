package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class PostFormatException extends PostException {

    private static final String CODE = "F0004";
    private static final String MESSAGE = "게시물 길이 에러";

    public PostFormatException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
