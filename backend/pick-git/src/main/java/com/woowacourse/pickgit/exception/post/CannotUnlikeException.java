package com.woowacourse.pickgit.exception.post;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CannotUnlikeException extends ApplicationException {

    private static final String CODE = "P0004";
    private static final String MESSAGE = "좋아요 하지 않은 게시물 좋아요 취소 에러";

    public CannotUnlikeException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
