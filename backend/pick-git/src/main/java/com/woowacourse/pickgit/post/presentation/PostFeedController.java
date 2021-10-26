package com.woowacourse.pickgit.post.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.PostAssembler;
import com.woowacourse.pickgit.post.presentation.dto.request.SearchPostsRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.PostResponse;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class PostFeedController {

    private final PostFeedService postFeedService;

    @ForLoginAndGuestUser
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> readHomeFeed(
        @Authenticated AppUser appUser,
        @PageableDefault Pageable pageable
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, pageable);
        List<PostResponseDto> postResponseDtos = postFeedService.homeFeed(homeFeedRequestDto);
        List<PostResponse> postResponses = PostAssembler.postResponses((postResponseDtos));

        return ResponseEntity.ok(postResponses);
    }

    @ForOnlyLoginUser
    @GetMapping("/posts/me")
    public ResponseEntity<List<PostResponse>> readMyFeed(
        @Authenticated AppUser appUser,
        @PageableDefault Pageable pageable
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, pageable);
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, appUser.getUsername());
        List<PostResponse> postResponses = PostAssembler.postResponses((postResponseDtos));

        return ResponseEntity.ok(postResponses);
    }

    @ForLoginAndGuestUser
    @GetMapping("/posts/{username}")
    public ResponseEntity<List<PostResponse>> readUserFeed(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @PageableDefault Pageable pageable
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, pageable);
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, username);
        List<PostResponse> postResponses = PostAssembler.postResponses(postResponseDtos);

        return ResponseEntity.ok(postResponses);
    }

    @ForLoginAndGuestUser
    @GetMapping("/search/posts")
    public ResponseEntity<List<PostResponse>> searchPosts(
        @Authenticated AppUser appUser,
        @PageableDefault Pageable pageable,
        SearchPostsRequest searchPostsByTagRequest
    ) {
        String type = searchPostsByTagRequest.getType();
        String keyword = searchPostsByTagRequest.getKeyword();

        SearchPostsRequestDto searchPostsRequestDto =
            new SearchPostsRequestDto(type, keyword, appUser);

        List<PostResponseDto> postResponseDtos = postFeedService.search(searchPostsRequestDto, pageable);
        List<PostResponse> postResponses = PostAssembler.postResponses((postResponseDtos));

        return ResponseEntity.ok(postResponses);
    }

    private List<PostResponse> createPostResponses(List<PostResponseDto> postResponseDtos) {
        return postResponseDtos.stream()
            .map(toPostResponseDtoPostResponse())
            .collect(toList());
    }

    private Function<PostResponseDto, PostResponse> toPostResponseDtoPostResponse() {
        return postResponseDto -> PostResponse.builder()
            .id(postResponseDto.getId())
            .imageUrls(postResponseDto.getImageUrls())
            .githubRepoUrl(postResponseDto.getGithubRepoUrl())
            .content(postResponseDto.getContent())
            .authorName(postResponseDto.getAuthorName())
            .profileImageUrl(postResponseDto.getProfileImageUrl())
            .likesCount(postResponseDto.getLikesCount())
            .tags(postResponseDto.getTags())
            .createdAt(postResponseDto.getCreatedAt())
            .updatedAt(postResponseDto.getUpdatedAt())
            .comments(postResponseDto.getComments())
            .liked(postResponseDto.getLiked())
            .build();
    }

    @ForLoginAndGuestUser
    @GetMapping(value = "/posts", params = "id")
    public ResponseEntity<PostResponse> findPostById(
        @Authenticated AppUser appUser,
        @RequestParam(value = "id") Long postId
    ) {
        PostResponseDto postResponseDto = postFeedService
            .searchById(new SearchPostRequestDto(postId, appUser));

        PostResponse postResponse = toPostResponseDtoPostResponse().apply(postResponseDto);

        return ResponseEntity.ok(postResponse);
    }
}
