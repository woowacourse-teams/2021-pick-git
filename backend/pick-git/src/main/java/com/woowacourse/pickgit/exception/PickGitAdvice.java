package com.woowacourse.pickgit.exception;

import static java.util.Objects.requireNonNull;

import com.woowacourse.pickgit.exception.dto.PickGitExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PickGitAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PickGitExceptionResponse> methodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        PickGitExceptionResponse exceptionResponse =
            new PickGitExceptionResponse(requireNonNull(e.getFieldError()).getDefaultMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(exceptionResponse);
    }
}
