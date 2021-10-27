package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchRepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.PostAssembler;
import com.woowacourse.pickgit.post.presentation.dto.request.PostRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostUpdateResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.RepositoryResponse;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@RequiredArgsConstructor
@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class PostController {

    private static final String REDIRECT_URL = "/api/posts/%s/%d";

    private final PostService postService;

    @ForOnlyLoginUser
    @PostMapping("/posts")
    public ResponseEntity<Void> write(@Authenticated AppUser user, PostRequest request) {
        Long postId = postService.write(PostAssembler.postRequestDto(user, request));
        String redirectUrl = String.format(REDIRECT_URL, user.getUsername(), postId);

        return ResponseEntity.created(URI.create(redirectUrl)).build();
    }

    @ForOnlyLoginUser
    @GetMapping("/github/repositories")
    public ResponseEntity<List<RepositoryResponse>> userRepositories(
        @Authenticated AppUser user,
        @PageableDefault Pageable pageable
    ) {
        RepositoryRequestDto repositoryRequestDto =
            PostAssembler.repositoryRequestDto(user, pageable);

        List<RepositoryResponseDto> repositoryResponseDtos = postService
            .userRepositories(repositoryRequestDto);

        List<RepositoryResponse> repositoryResponses =
            PostAssembler.repositoryResponses(repositoryResponseDtos);

        return ResponseEntity.ok(repositoryResponses);
    }

    @ForOnlyLoginUser
    @GetMapping("/github/search/repositories")
    public ResponseEntity<List<RepositoryResponse>> userSearchedRepositories(
        @Authenticated AppUser user,
        @RequestParam String keyword,
        @PageableDefault Pageable pageable
    ) {
        SearchRepositoryRequestDto searchRepositoryRequestDto =
            PostAssembler.searchRepositoryRequestDto(user, keyword, pageable);

        List<RepositoryResponseDto> repositoryResponseDtos = postService
            .searchUserRepositories(searchRepositoryRequestDto);

        List<RepositoryResponse> repositoryResponses =
            PostAssembler.repositoryResponses(repositoryResponseDtos);

        return ResponseEntity.ok(repositoryResponses);
    }

    @ForOnlyLoginUser
    @PutMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> likePost(
        @Authenticated AppUser user,
        @PathVariable Long postId
    ) {
        LikeResponseDto likeResponseDto = postService.like(user, postId);
        LikeResponse likeResponse =PostAssembler.likeResponse(likeResponseDto);

        return ResponseEntity.ok(likeResponse);
    }

    @ForOnlyLoginUser
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> unlikePost(
        @Authenticated AppUser user,
        @PathVariable Long postId
    ) {
        LikeResponseDto likeResponseDto = postService.unlike(user, postId);
        LikeResponse likeResponse = PostAssembler.likeResponse(likeResponseDto);

        return ResponseEntity.ok(likeResponse);
    }

    @ForOnlyLoginUser
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostUpdateResponse> update(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @Valid @RequestBody PostUpdateRequest updateRequest
    ) {
        PostUpdateResponseDto updateResponseDto =
            postService.update(PostAssembler.postUpdateRequestDto(user, postId, updateRequest));

        PostUpdateResponse postUpdateResponse = PostAssembler.postUpdateResponse(updateResponseDto);

        URI redirectUrl = URI.create(String.format(REDIRECT_URL, user.getUsername(), postId));

        return ResponseEntity.created(redirectUrl).body(postUpdateResponse);
    }

    @ForOnlyLoginUser
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> delete(
        @Authenticated AppUser user,
        @PathVariable Long postId
    ) {
        postService.delete(PostAssembler.postDeleteRequestDto(user, postId));

        return ResponseEntity.noContent().build();
    }



    @ForLoginAndGuestUser
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<LikeUsersResponse>> searchLikeUsers(
        @Authenticated AppUser appUser,
        @PathVariable Long postId
    ) {
        AuthUserForPostRequestDto authUserRequestDto = new AuthUserForPostRequestDto(appUser);

        List<LikeUsersResponseDto> likeUsersResponseDtos =
            postService.likeUsers(authUserRequestDto, postId);

        return ResponseEntity.ok(PostAssembler.likeUsersResponses(likeUsersResponseDtos));
    }
}
