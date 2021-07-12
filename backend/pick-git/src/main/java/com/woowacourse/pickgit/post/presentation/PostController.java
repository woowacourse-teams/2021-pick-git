package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.PostResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.PostRequest;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> writePost(
        @Authenticated AppUser user,
        @Valid @RequestBody PostRequest request) {
        if (user.isGuest()) {
            throw new IllegalArgumentException("게스트는 글을 작성할 수 없습니다!");
        }

        PostResponseDto responseDto = postService.writePost(
            new PostRequestDto(user.getAccessToken(), user.getUsername(), request.getImages(),
                request.getGithubRepoUrl(), request.getTags(), request.getContent()));

        return ResponseEntity
            .created(redirect(user, responseDto))
            .build();
    }

    private URI redirect(AppUser user, PostResponseDto responseDto) {
        return URI.create("/api/posts/" + user.getUsername() + "/" + responseDto.getId());
    }
}
