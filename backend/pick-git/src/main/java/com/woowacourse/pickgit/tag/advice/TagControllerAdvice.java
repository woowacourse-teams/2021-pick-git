package com.woowacourse.pickgit.tag.advice;

import com.woowacourse.pickgit.tag.domain.TagException;
import com.woowacourse.pickgit.tag.presentation.TagController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice(basePackageClasses = {TagController.class})
public class TagControllerAdvice {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleApiExceptions(
        HttpClientErrorException httpClientErrorException) {
        return ResponseEntity.badRequest()
            .body("P0001");
    }

    @ExceptionHandler(TagException.class)
    public ResponseEntity<String> handleTagException(TagException tagException) {
        return ResponseEntity.status(tagException.getStatusCode())
            .body(tagException.getMessage());
    }
}
