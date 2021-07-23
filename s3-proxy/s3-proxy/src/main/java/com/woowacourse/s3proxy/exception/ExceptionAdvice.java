package com.woowacourse.s3proxy.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionDto> applicationException(
        ApplicationException applicationException
    ) {
        return ResponseEntity.status(applicationException.getHttpStatus())
            .body(
                new ExceptionDto(applicationException.getErrorCode())
            );
    }

    public static class ExceptionDto {

        private String errorCode;

        private ExceptionDto() {
        }

        public ExceptionDto(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }
}
