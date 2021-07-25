package com.woowacourse.pickgit.user.application.dto.response;

import lombok.Builder;

@Builder
public class FollowResponseDto {

    private int followerCount;
    private boolean isFollowing;

    public FollowResponseDto(int followerCount, boolean isFollowing) {
        this.followerCount = followerCount;
        this.isFollowing = isFollowing;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public boolean isFollowing() {
        return isFollowing;
    }
}
