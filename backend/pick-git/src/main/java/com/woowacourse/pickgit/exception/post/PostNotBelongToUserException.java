package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class PostNotBelongToUserException extends PostException{

    private static final String CODE = "P0005";
    private static final String MESSAGE = "해당하는 사용자의 게시물이 아닌 에러";

    public PostNotBelongToUserException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
