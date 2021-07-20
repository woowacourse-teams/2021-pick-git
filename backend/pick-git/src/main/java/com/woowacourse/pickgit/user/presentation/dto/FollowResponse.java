package com.woowacourse.pickgit.user.presentation.dto;

public class FollowResponse {

    private int followerCount;
    private boolean following;

    private FollowResponse() {
    }

    public FollowResponse(int followerCount, boolean isFollowing) {
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
