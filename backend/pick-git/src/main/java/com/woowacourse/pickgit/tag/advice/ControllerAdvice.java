package com.woowacourse.pickgit.tag.advice;

import com.woowacourse.pickgit.tag.domain.TagException;
import com.woowacourse.pickgit.tag.presentation.TagController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice(basePackageClasses = {TagController.class})
public class ControllerAdvice {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleApiExceptions(
        HttpClientErrorException httpClientErrorException) {
        return ResponseEntity.status(httpClientErrorException.getStatusCode())
            .body("외부 플랫폼 연동 요청 처리에 실패했습니다.");
    }

    @ExceptionHandler(TagException.class)
    public ResponseEntity<String> handleTagException(TagException tagException) {
        return ResponseEntity.status(tagException.getStatusCode())
            .body(tagException.getMessage());
    }
}
