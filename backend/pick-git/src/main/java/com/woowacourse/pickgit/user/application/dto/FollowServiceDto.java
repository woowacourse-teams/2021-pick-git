package com.woowacourse.pickgit.user.application.dto;

public class FollowServiceDto {

    boolean isFollowing;
    private int followerCount;

    public FollowServiceDto(int followerCount, boolean isFollowing) {
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
