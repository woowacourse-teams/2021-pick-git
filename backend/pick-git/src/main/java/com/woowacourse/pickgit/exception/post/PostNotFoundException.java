package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends PostException {

    private static final String CODE = "P0002";
    private static final String MESSAGE = "해당하는 게시물을 찾을 수 없습니다.";

    public PostNotFoundException() {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }
}
