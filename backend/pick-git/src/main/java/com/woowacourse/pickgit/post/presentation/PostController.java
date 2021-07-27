package com.woowacourse.pickgit.post.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.CommentResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.RepositoryResponse;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
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
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> write(
        @Authenticated AppUser user,
        PostRequest request
    ) {
        var postImageUrlResponseDto = postService.write(createPostRequestDto(user, request));

        return ResponseEntity
            .created(redirectUrl(user, postImageUrlResponseDto))
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

    private URI redirectUrl(AppUser user, PostImageUrlResponseDto responseDto) {
        return URI
            .create(String.format("/api/posts/%s/%d", user.getUsername(), responseDto.getId()));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @Valid @RequestBody ContentRequest request
    ) {
        var commentRequestDto = createCommentRequest(user, postId, request);
        var commentResponseDto = postService.addComment(commentRequestDto);
        var commentResponse = createCommentResponse(commentResponseDto);

        return ResponseEntity.ok(commentResponse);
    }

    private CommentRequestDto createCommentRequest(
        AppUser user,
        Long postId,
        ContentRequest request
    ) {
        return CommentRequestDto.builder()
            .userName(user.getUsername())
            .content(request.getContent())
            .postId(postId)
            .build();
    }

    private CommentResponse createCommentResponse(CommentResponseDto commentResponseDto) {
        return CommentResponse.builder()
            .id(commentResponseDto.getId())
            .profileImageUrl(commentResponseDto.getProfileImageUrl())
            .content(commentResponseDto.getContent())
            .authorName(commentResponseDto.getAuthorName())
            .isLiked(commentResponseDto.getIsLiked())
            .build();
    }

    @GetMapping("/github/{username}/repositories")
    public ResponseEntity<List<RepositoryResponse>> userRepositories(
        @Authenticated AppUser user,
        @PathVariable String username
    ) {
        String token = user.getAccessToken();

        var repositoryRequestDto = new RepositoryRequestDto(token, username);
        var repositoryResponseDtos = postService.userRepositories(repositoryRequestDto);
        var repositoryResponses =
            toRepositoryResponses(repositoryResponseDtos.getRepositoryResponseDtos());

        return ResponseEntity.ok(repositoryResponses);
    }

    private List<RepositoryResponse> toRepositoryResponses(
        List<RepositoryResponseDto> repositoryResponseDos
    ) {
        return repositoryResponseDos.stream()
            .map(toRepositoryResponse())
            .collect(toList());
    }

    private Function<RepositoryResponseDto, RepositoryResponse> toRepositoryResponse() {
        return repositoryResponseDto -> RepositoryResponse.builder()
            .name(repositoryResponseDto.getName())
            .url(repositoryResponseDto.getUrl())
            .build();
    }

    @PutMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> likePost(
        @Authenticated AppUser user,
        @PathVariable Long postId
    ) {
        validateIsGuest(user);
        LikeResponseDto likeResponseDto = postService.like(user, postId);

        LikeResponse likeResponse =
            new LikeResponse(likeResponseDto.getLikesCount(), likeResponseDto.getLiked());

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
            new LikeResponse(likeResponseDto.getLikesCount(), likeResponseDto.getLiked());

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
