package com.woowacourse.pickgit.post.exception;

import com.woowacourse.pickgit.post.presentation.PostController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice(basePackageClasses = {PostController.class})
public class PostAdvice {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> httpClientErrorException(HttpClientErrorException e) {
        return ResponseEntity.badRequest().body("P0001");
    }
}
