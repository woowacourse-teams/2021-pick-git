package com.woowacourse.pickgit.post.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchRepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponsesDto;
import com.woowacourse.pickgit.post.presentation.dto.request.PostRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostUpdateResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class PostController {

    private static final String REDIRECT_URL = "/api/posts/%s/%d";

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ForOnlyLoginUser
    @PostMapping("/posts")
    public ResponseEntity<Void> write(@Authenticated AppUser user, PostRequest request) {
        PostImageUrlResponseDto postImageUrlResponseDto =
            postService.write(createPostRequestDto(user, request));

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



    @ForOnlyLoginUser
    @GetMapping("/github/repositories")
    public ResponseEntity<List<RepositoryResponse>> userRepositories(
        @Authenticated AppUser user,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        RepositoryRequestDto repositoryRequestDto = toRepositoryRequestDto(user, page, limit);

        RepositoryResponsesDto repositoryResponsesDto = postService
            .userRepositories(repositoryRequestDto);

        List<RepositoryResponse> repositoryResponses = toRepositoryResponses(
            repositoryResponsesDto.getRepositoryResponsesDto()
        );

        return ResponseEntity.ok(repositoryResponses);
    }

    private RepositoryRequestDto toRepositoryRequestDto(AppUser user, Long page, Long limit) {
        return RepositoryRequestDto.builder()
            .token(user.getAccessToken())
            .username(user.getUsername())
            .page(page)
            .limit(limit)
            .build();
    }

    @ForOnlyLoginUser
    @GetMapping("/github/search/repositories")
    public ResponseEntity<List<RepositoryResponse>> userSearchedRepositories(
        @Authenticated AppUser user,
        @RequestParam String keyword,
        @RequestParam int page,
        @RequestParam int limit
    ) {
        SearchRepositoryRequestDto searchRepositoryRequestDto
            = new SearchRepositoryRequestDto(
                user.getAccessToken(), user.getUsername(), keyword, page, limit
        );

        RepositoryResponsesDto repositoryResponsesDtos =
            postService.searchUserRepositories(searchRepositoryRequestDto);
        List<RepositoryResponse> repositoryResponses = toRepositoryResponses(
            repositoryResponsesDtos.getRepositoryResponsesDto()
        );

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
            .url(repositoryResponseDto.getUrl())
            .name(repositoryResponseDto.getName())
            .build();
    }

    @ForOnlyLoginUser
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

    @ForOnlyLoginUser
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

    @ForOnlyLoginUser
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

    @ForOnlyLoginUser
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

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<LikeUsersResponse>> searchLikeUsers(
        @Authenticated AppUser appUser,
        @PathVariable Long postId
    ) {
        AuthUserForPostRequestDto authUserRequestDto = createAuthUserForPostRequestDto(appUser);

        List<LikeUsersResponseDto> likeUsersResponseDtos = postService
            .likeUsers(authUserRequestDto, postId);

        return ResponseEntity.ok(createLikeUsersResponse(likeUsersResponseDtos));
    }

    private AuthUserForPostRequestDto createAuthUserForPostRequestDto(AppUser appUser) {
         if (appUser.isGuest()) {
             return new AuthUserForPostRequestDto(null, true);
         }

         return new AuthUserForPostRequestDto(appUser.getUsername(), false);
    }

    private List<LikeUsersResponse> createLikeUsersResponse(
        List<LikeUsersResponseDto> likeUsersResponseDtos) {
        return likeUsersResponseDtos.stream()
            .map(dto -> LikeUsersResponse.builder()
                .username(dto.getUsername())
                .imageUrl(dto.getImageUrl())
                .following(dto.getFollowing())
                .build()
            ).collect(toList());
    }
}
