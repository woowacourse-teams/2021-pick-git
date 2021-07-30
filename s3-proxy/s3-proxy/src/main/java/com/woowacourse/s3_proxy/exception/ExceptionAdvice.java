package com.woowacourse.s3_proxy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    private static final String LOG_FORMAT= "Class : {}, Code : {}, Message : {}";
    private static final String INTERNAL_SERVER_ERROR_CODE = "S0001";

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionDto> applicationException(
        ApplicationException e
    ) {
        String errorCode = e.getErrorCode();
        log.warn(
            LOG_FORMAT,
            e.getClass().getSimpleName(),
            errorCode,
            e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus())
            .body(new ExceptionDto(errorCode));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDto> runtimeException(RuntimeException e) {
        log.error(
            LOG_FORMAT,
            e.getClass().getSimpleName(),
            INTERNAL_SERVER_ERROR_CODE,
            e.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ExceptionDto(INTERNAL_SERVER_ERROR_CODE));
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
