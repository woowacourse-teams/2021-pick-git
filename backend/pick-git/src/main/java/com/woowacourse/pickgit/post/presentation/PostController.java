package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.post.application.CommentRequestDto;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.application.dto.CommentDto;
import com.woowacourse.pickgit.post.application.dto.PostDto;
import com.woowacourse.pickgit.post.presentation.dto.HomeFeedRequest;
import com.woowacourse.pickgit.post.presentation.dto.PostRequest;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {

    private static final String REDIRECT_URL = "/api/posts/%s/%d";

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDto>> readHomeFeed(@Authenticated AppUser appUser,
        @RequestParam Long page, @RequestParam Long limit) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostDto> postDtos = postService.readHomeFeed(homeFeedRequest);
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/posts/me")
    public ResponseEntity<List<PostDto>> readMyFeed(@Authenticated AppUser appUser,
        @RequestParam Long page, @RequestParam Long limit) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostDto> postDtos = postService.readMyFeed(homeFeedRequest);
        return ResponseEntity.ok(postDtos);
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

    private URI redirectUrl(AppUser user, PostResponseDto responseDto) {
        return URI.create(String.format(REDIRECT_URL, user.getUsername(), responseDto.getId()));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> addComment(@Authenticated AppUser appUser,
        @PathVariable Long postId, @RequestBody String content) {
        CommentRequestDto commentRequestDto =
            new CommentRequestDto(appUser.getUsername(), content, postId);
        CommentDto commentDto = postService.addComment(commentRequestDto);
        return ResponseEntity.ok(commentDto);
    }

    @GetMapping("/github/{username}/repositories")
    public ResponseEntity<List<RepositoryResponseDto>> showRepositories(
        @Authenticated AppUser user,
        @PathVariable String username) {
        String token = user.getAccessToken();
        RepositoriesResponseDto responseDto = postService
            .showRepositories(new RepositoryRequestDto(token, username));

        return ResponseEntity.ok(responseDto.getRepositories());
    }
}
