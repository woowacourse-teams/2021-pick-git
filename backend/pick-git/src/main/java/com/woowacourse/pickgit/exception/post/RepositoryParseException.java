package com.woowacourse.pickgit.exception.post;

import org.springframework.http.HttpStatus;

public class RepositoryParseException extends PostException {

    private static final String CODE = "V0001";
    private static final String MESSAGE = "레포지토리 목록을 불러올 수 없습니다.";

    public RepositoryParseException() {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }
}
