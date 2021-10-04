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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";
    private static final String INTERNAL_SERVER_ERROR_CODE = "S0001";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> methodArgumentNotValidException(
        MethodArgumentNotValidException e
    ) {
        String errorCode = requireNonNull(e.getFieldError())
            .getDefaultMessage();
        ApiErrorResponse exceptionResponse = new ApiErrorResponse(errorCode);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), errorCode, "@Valid");
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(exceptionResponse);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> applicationException(ApplicationException e) {
        String errorCode = e.getErrorCode();
        log.warn(
            LOG_FORMAT,
            e.getClass().getSimpleName(),
            errorCode,
            e.getMessage()
        );
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(new ApiErrorResponse(errorCode));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> dataAccessException(DataAccessException e) {
        log.error(
            LOG_FORMAT,
            e.getClass().getSimpleName(),
            INTERNAL_SERVER_ERROR_CODE,
            e.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(INTERNAL_SERVER_ERROR_CODE));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> runtimeException(RuntimeException e) {
        log.error(
            LOG_FORMAT,
            e.getClass().getSimpleName(),
            INTERNAL_SERVER_ERROR_CODE,
            e.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(INTERNAL_SERVER_ERROR_CODE));
    }
}
