package com.woowacourse.s3proxy.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> PickGitStorageExceptionHandler(
        PickGitStorageException e
    ) {
        return ResponseEntity.badRequest().body(
            new ExceptionDto(e.getMessage())
        );
    }

    private static class ExceptionDto {

        private final String message;

        public ExceptionDto(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
