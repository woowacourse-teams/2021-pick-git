package com.woowacourse.pickgit.post.presentation.postfeed;

import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import java.util.List;

public interface FeedType {

    boolean isSatisfiedBy(String type);

    List<PostResponseDto> find(HomeFeedRequestDto homeFeedRequestDto);
}
