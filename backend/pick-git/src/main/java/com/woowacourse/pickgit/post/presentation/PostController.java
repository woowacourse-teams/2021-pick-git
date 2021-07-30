package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostUpdateResponse;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        @RequestParam Long limit
    ) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostResponseDto> postResponsesDto = postService.readHomeFeed(homeFeedRequest);
        return ResponseEntity.ok(postResponsesDto);
    }

    @GetMapping("/posts/me")
    public ResponseEntity<List<PostResponseDto>> readMyFeed(
        @Authenticated AppUser appUser,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostResponseDto> postResponseDtos = postService.readMyFeed(homeFeedRequest);
        return ResponseEntity.ok(postResponseDtos);
    }

    @GetMapping("/posts/{username}")
    public ResponseEntity<List<PostResponseDto>> readUserFeed(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequest homeFeedRequest = new HomeFeedRequest(appUser, page, limit);
        List<PostResponseDto> postResponsesDto = postService
            .readUserFeed(homeFeedRequest, username);
        return ResponseEntity.ok(postResponsesDto);
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> write(
        @Authenticated AppUser user,
        PostRequest request
    ) {
        validateIsGuest(user);

        PostImageUrlResponseDto responseDto = postService
            .write(createPostRequestDto(user, request));

        return ResponseEntity
            .created(redirectUrl(user.getUsername(), responseDto.getId()))
            .build();
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

    @PutMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> likePost(
        @Authenticated AppUser user,
        @PathVariable Long postId
    ) {
        validateIsGuest(user);
        LikeResponseDto likeResponseDto = postService.like(user, postId);

        LikeResponse likeResponse =
            new LikeResponse(likeResponseDto.getLikeCount(), likeResponseDto.isLiked());

        return ResponseEntity.ok(likeResponse);
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> unlikePost(
        @Authenticated AppUser user,
        @PathVariable Long postId
    ) {
        validateIsGuest(user);
        LikeResponseDto likeResponseDto = postService.unlike(user, postId);

        LikeResponse likeResponse =
            new LikeResponse(likeResponseDto.getLikeCount(), likeResponseDto.isLiked());

        return ResponseEntity.ok(likeResponse);
    }

    private void validateIsGuest(AppUser user) {
        if (user.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostUpdateResponse> update(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @Valid @RequestBody PostUpdateRequest updateRequest
    ) {
        PostUpdateResponseDto updateResponseDto = postService
            .update(createPostUpdateRequestDto(user, postId, updateRequest));

        return ResponseEntity
            .created(redirectUrl(user.getUsername(), postId))
            .body(createPostUpdateResponse(updateResponseDto));
    }

    private PostUpdateRequestDto createPostUpdateRequestDto(
        AppUser user,
        Long postId,
        PostUpdateRequest updateRequest) {
        return new PostUpdateRequestDto(
            user,
            postId,
            updateRequest.getTags(),
            updateRequest.getContent()
        );
    }

    private PostUpdateResponse createPostUpdateResponse(PostUpdateResponseDto updateResponseDto) {
        return PostUpdateResponse.builder()
            .tags(updateResponseDto.getTags())
            .content(updateResponseDto.getContent())
            .build();
    }

    private URI redirectUrl(String username, Long postId) {
        return URI.create(String.format(REDIRECT_URL, username, postId));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> delete(
        @Authenticated AppUser user,
        @PathVariable Long postId
    ) {
        postService.delete(createPostDeleteRequestDto(user, postId));

        return ResponseEntity
            .noContent()
            .build();
    }

    private PostDeleteRequestDto createPostDeleteRequestDto(AppUser user, Long postId) {
        return new PostDeleteRequestDto(user, postId);
    }
}
