package com.woowacourse.pickgit.exception;

import static java.util.Objects.requireNonNull;

import com.woowacourse.pickgit.exception.authentication.AuthenticationException;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> methodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        ApiErrorResponse exceptionResponse =
            new ApiErrorResponse(requireNonNull(e.getFieldError()).getDefaultMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(exceptionResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> authenticationException(
        AuthenticationException e) {
        return ResponseEntity
            .status(e.getHttpStatus().value())
            .body(new ApiErrorResponse(e.getErrorCode()));
    }
}