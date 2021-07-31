package com.woowacourse.pickgit.user.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowResponse {

    private int followerCount;
    private boolean following;

    private FollowResponse() {
    }

    public FollowResponse(int followerCount, boolean isFollowing) {
        this.followerCount = followerCount;
        this.following = isFollowing;
    }
}
