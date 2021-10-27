package com.woowacourse.pickgit.post.presentation.postfeed;

import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AllFeedType implements FeedType {

    private static final String TYPE = "all";
    
    private final PostFeedService postFeedService;

    public AllFeedType(PostFeedService postFeedService) {
        this.postFeedService = postFeedService;
    }

    @Override
    public boolean isSatisfiedBy(String type) {
        return TYPE.equalsIgnoreCase(type);
    }

    @Override
    public List<PostResponseDto> find(HomeFeedRequestDto homeFeedRequestDto) {
        return postFeedService.allHomeFeed(homeFeedRequestDto);
    }
}
