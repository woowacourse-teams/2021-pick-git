package com.woowacourse.pickgit.exception;

import static java.util.Objects.requireNonNull;

import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> methodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        ApiErrorResponse exceptionResponse =
            new ApiErrorResponse(requireNonNull(e.getFieldError()).getDefaultMessage());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(exceptionResponse);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> authenticationException(
        ApplicationException e) {
        return ResponseEntity
            .status(e.getHttpStatus().value())
            .body(new ApiErrorResponse(e.getErrorCode()));
    }
}
