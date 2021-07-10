package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.LikeRequestDto;
import com.woowacourse.pickgit.post.application.dto.LikeResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.LikeResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> addLike(
        HttpServletRequest httpServletRequest,
        @PathVariable Long postId) {
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
        LikeResponseDto response = postService.addLike(new LikeRequestDto(token, postId));

        return ResponseEntity.ok(new LikeResponse(response.getLikesCount(), response.isLiked()));
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> deleteLike(
        HttpServletRequest httpServletRequest,
        @PathVariable Long postId) {
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
        LikeResponseDto response = postService.deleteLike(new LikeRequestDto(token, postId));

        return ResponseEntity.ok(new LikeResponse(response.getLikesCount(), response.isLiked()));
    }
}
