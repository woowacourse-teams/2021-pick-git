package com.woowacourse.pickgit.post.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.PostAssembler;
import com.woowacourse.pickgit.post.presentation.dto.request.SearchPostsRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.PostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, page, limit);
        List<PostResponseDto> postResponseDtos = postFeedService.homeFeed(homeFeedRequestDto);
        List<PostResponse> postResponses = PostAssembler.postResponses((postResponseDtos));

        return ResponseEntity.ok(postResponses);
    }

    @ForOnlyLoginUser
    @GetMapping("/posts/me")
    public ResponseEntity<List<PostResponse>> readMyFeed(
        @Authenticated AppUser appUser,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, page, limit);
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
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        HomeFeedRequestDto homeFeedRequestDto = new HomeFeedRequestDto(appUser, page, limit);
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, username);
        List<PostResponse> postResponses = PostAssembler.postResponses(postResponseDtos);

        return ResponseEntity.ok(postResponses);
    }

    @ForLoginAndGuestUser
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
        List<PostResponse> postResponses = PostAssembler.postResponses((postResponseDtos));

        return ResponseEntity.ok(postResponses);
    }
}
