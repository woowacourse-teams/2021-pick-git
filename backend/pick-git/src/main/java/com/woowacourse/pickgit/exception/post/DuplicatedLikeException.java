package com.woowacourse.pickgit.exception.post;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class DuplicatedLikeException extends ApplicationException {

    private static final String CODE = "P0003";
    private static final String MESSAGE = "이미 좋아요한 게시물 중복 좋아요 에러";

    public DuplicatedLikeException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
