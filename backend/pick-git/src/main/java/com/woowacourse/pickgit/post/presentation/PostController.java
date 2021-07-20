package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostRequest;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(value = "*")
public class PostController {

    private static final String REDIRECT_URL = "/api/posts/%s/%d";

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> readHomeFeed(
        @Authenticated AppUser appUser,
        @RequestParam Long page,
        @RequestParam Long limit) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostResponseDto> postResponseDtos = postService.readHomeFeed(homeFeedRequest);
        return ResponseEntity.ok(postResponseDtos);
    }

    @GetMapping("/posts/me")
    public ResponseEntity<List<PostResponseDto>> readMyFeed(@Authenticated AppUser appUser,
        @RequestParam Long page, @RequestParam Long limit) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostResponseDto> postResponseDtos = postService.readMyFeed(homeFeedRequest);
        return ResponseEntity.ok(postResponseDtos);
    }

    @GetMapping("/posts/{username}")
    public ResponseEntity<List<PostResponseDto>> readUserFeed(@Authenticated AppUser appUser,
        @PathVariable String username, @RequestParam Long page, @RequestParam Long limit) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostResponseDto> postResponseDtos = postService
            .readUserFeed(homeFeedRequest, username);
        return ResponseEntity.ok(postResponseDtos);
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> write(
        @Authenticated AppUser user,
        PostRequest request
    ) {
        validateIsGuest(user);

        PostImageUrlResponseDto responseDto = postService.write(
            createPostRequestDto(user, request)
        );

        return ResponseEntity
            .created(redirectUrl(user, responseDto))
            .build();
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @Valid @RequestBody ContentRequest request
    ) {
        validateIsGuest(user);

        CommentRequest commentRequest =
            new CommentRequest(user.getUsername(), request.getContent(), postId);
        CommentResponse response = postService.addComment(commentRequest);

        return ResponseEntity.ok(response);
    }

    private void validateIsGuest(AppUser user) {
        if (user.isGuest()) {
            throw new UnauthorizedException();
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

    private URI redirectUrl(AppUser user, PostImageUrlResponseDto responseDto) {
        return URI.create(String.format(REDIRECT_URL, user.getUsername(), responseDto.getId()));
    }

    @GetMapping("/github/{username}/repositories")
    public ResponseEntity<List<RepositoryResponseDto>> showRepositories(
        @Authenticated AppUser user,
        @PathVariable String username
    ) {
        String token = user.getAccessToken();
        RepositoriesResponseDto responseDto = postService
            .showRepositories(new RepositoryRequestDto(token, username));

        return ResponseEntity.ok(responseDto.getRepositories());
    }
}
