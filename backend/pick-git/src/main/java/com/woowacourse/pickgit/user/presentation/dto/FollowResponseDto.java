package com.woowacourse.pickgit.user.presentation.dto;

public class FollowResponseDto {

    private final int followerCount;
    private final boolean isFollowing;

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
