package com.woowacourse.pickgit.exception;

import static java.util.Objects.requireNonNull;

import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_CODE = "S0001";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> methodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        String message = requireNonNull(e.getFieldError())
            .getDefaultMessage();
        ApiErrorResponse exceptionResponse = new ApiErrorResponse(message);
        log.error(message);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(exceptionResponse);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> authenticationException(
        ApplicationException e) {
        String errorCode = e.getErrorCode();
        log.error("로그인 실패 " + errorCode);

        return ResponseEntity
            .status(e.getHttpStatus().value())
            .body(new ApiErrorResponse(errorCode));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> dataAccessException(
        DataAccessException dataAccessException) {
        log.warn(dataAccessException.getMessage());
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(INTERNAL_SERVER_ERROR_CODE));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> runtimeException(RuntimeException runtimeException) {
        log.warn(runtimeException.getMessage());
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(INTERNAL_SERVER_ERROR_CODE));
    }
}
