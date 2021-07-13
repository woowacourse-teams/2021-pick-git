package com.woowacourse.pickgit.user.presentation.dto;

public class FollowResponseDto {

    private int followerCount;
    private boolean following;

    private FollowResponseDto() {
    }

    public FollowResponseDto(int followerCount, boolean isFollowing) {
        this.followerCount = followerCount;
        this.following = isFollowing;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public boolean isFollowing() {
        return following;
    }
}
