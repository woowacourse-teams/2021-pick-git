package com.woowacourse.pickgit.post.presentation.postfeed;

import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FollowingFeedType implements FeedType {

    private static final String TYPE = "following";

    private final PostFeedService postFeedService;

    public FollowingFeedType(PostFeedService postFeedService) {
        this.postFeedService = postFeedService;
    }

    @Override
    public boolean isSatisfiedBy(String type) {
        return TYPE.equalsIgnoreCase(type);
    }

    @Override
    public List<PostResponseDto> find(HomeFeedRequestDto homeFeedRequestDto) {
        return postFeedService.followingHomeFeed(homeFeedRequestDto);
    }
}
