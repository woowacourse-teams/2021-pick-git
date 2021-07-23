package com.woowacourse.pickgit.user.application.dto;

public class FollowResponseDto {

    private boolean isFollowing;
    private int followerCount;

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
