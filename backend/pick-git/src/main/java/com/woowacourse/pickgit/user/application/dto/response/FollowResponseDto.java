package com.woowacourse.pickgit.user.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowResponseDto {

    private int followerCount;
    private boolean isFollowing;

    private FollowResponseDto() {
    }

    public FollowResponseDto(int followerCount, boolean isFollowing) {
        this.followerCount = followerCount;
        this.isFollowing = isFollowing;
    }
}
