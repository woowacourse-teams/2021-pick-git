package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.post.application.CommentRequestDto;
import com.woowacourse.pickgit.post.application.CommentResponseDto;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.RepositoryDto;
import com.woowacourse.pickgit.post.application.dto.TokenDto;
import com.woowacourse.pickgit.post.presentation.dto.PostRequest;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {

    private static final String REDIRECT_URL = "/api/posts/%s/%d";

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> write(
        @Authenticated AppUser user,
        @Valid PostRequest request
    ) {
        validateIsGuest(user);

        PostResponseDto responseDto = postService.write(
            createPostRequestDto(user, request)
        );

        return ResponseEntity
            .created(redirectUrl(user, responseDto))
            .build();
    }

    private void validateIsGuest(AppUser user) {
        if (user.isGuest()) {
            throw new IllegalArgumentException("게스트는 글을 작성할 수 없습니다!");
        }
    }

    private PostRequestDto createPostRequestDto(AppUser user, PostRequest request) {
        return new PostRequestDto(
            user.getAccessToken(),
            user.getUsername(),
            request.getImages(),
            request.getGithubRepoUrl(),
            request.getTags(),
            request.getContent()
        );
    }

    private URI redirectUrl(AppUser user, PostResponseDto responseDto) {
        return URI.create(String.format(REDIRECT_URL, user.getUsername(), responseDto.getId()));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(@Authenticated AppUser appUser,
        @PathVariable Long postId, @RequestBody String content)
    {
        CommentRequestDto commentRequestDto =
            new CommentRequestDto(appUser.getUsername(), content, postId);
        CommentResponseDto commentResponseDto = postService.addComment(commentRequestDto);
        return ResponseEntity.ok(commentResponseDto);
    }

    @GetMapping("/github/repositories")
    public ResponseEntity<RepositoryDto> getRepositories(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
        RepositoryDto repositories = postService.getRepositories(new TokenDto(token));

        return ResponseEntity.ok(repositories);
    }
}
