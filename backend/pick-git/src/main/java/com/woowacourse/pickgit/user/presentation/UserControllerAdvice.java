package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.user.exception.PickgitException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(PickgitException.class)
    public ResponseEntity<String> handleException(PickgitException exception) {
        return ResponseEntity
            .badRequest()
            .body(exception.getMessage());
    }
}
