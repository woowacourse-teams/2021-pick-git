package com.woowacourse.pickgit.post.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.SearchPostsRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostResponse;
import java.util.List;
import java.util.function.Function;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class PostFeedController {

    private final PostFeedService postFeedService;

    public PostFeedController(PostFeedService postFeedService) {
        this.postFeedService = postFeedService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> readHomeFeed(
        @Authenticated AppUser appUser,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, page, limit);
        List<PostResponseDto> postResponseDtos = postFeedService.homeFeed(homeFeedRequestDto);
        List<PostResponse> postResponses = createPostResponses(postResponseDtos);

        return ResponseEntity.ok(postResponses);
    }

    @GetMapping("/posts/me")
    public ResponseEntity<List<PostResponse>> readMyFeed(
        @Authenticated AppUser appUser,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, page, limit);
        List<PostResponseDto> postResponseDtos = postFeedService.myFeed(homeFeedRequestDto);
        List<PostResponse> postResponses = createPostResponses(postResponseDtos);

        return ResponseEntity.ok(postResponses);
    }

    @GetMapping("/posts/{username}")
    public ResponseEntity<List<PostResponse>> readUserFeed(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, page, limit);
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, username);
        List<PostResponse> postResponses = createPostResponses(postResponseDtos);

        return ResponseEntity.ok(postResponses);
    }

    @GetMapping("/search/posts")
    public ResponseEntity<List<PostResponse>> searchPosts(
        @Authenticated AppUser appUser,
        SearchPostsRequest searchPostsByTagRequest
    ) {
        String type = searchPostsByTagRequest.getType();
        String keyword = searchPostsByTagRequest.getKeyword();
        int page = searchPostsByTagRequest.getPage();
        int limit = searchPostsByTagRequest.getLimit();

        SearchPostsRequestDto searchPostsRequestDto =
            new SearchPostsRequestDto(type, keyword, page, limit, appUser);

        List<PostResponseDto> postResponseDtos = postFeedService.search(searchPostsRequestDto);
        List<PostResponse> postResponses = createPostResponses(postResponseDtos);

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
}
