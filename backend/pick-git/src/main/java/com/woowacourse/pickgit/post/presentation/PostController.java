package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.PostResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.PostRequest;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/posts/{username}")
    public ResponseEntity<Void> writePost(
        HttpServletRequest httpServletRequest,
        @PathVariable String username,
        @Valid PostRequest request) {
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
        PostResponseDto postResponse = postService.writePost(
            new PostRequestDto(token, username, request.getImages(),
                request.getGithubRepoUrl(), request.getTags(), request.getContent()));

        return ResponseEntity.created(URI.create("/api/posts/" + username + "/" + postResponse.getId()))
            .build();
    }
}
