package com.woowacourse.pickgit.post.advice;

import com.woowacourse.pickgit.post.domain.PostException;
import com.woowacourse.pickgit.post.presentation.PostController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {PostController.class})
public class PostControllerAdvice {

    @ExceptionHandler(PostException.class)
    public ResponseEntity<String> handlePostException(PostException postException) {
        return ResponseEntity.status(postException.getStatusCode())
            .body(postException.getMessage());
    }
}
