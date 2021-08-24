package com.woowacourse.pickgit.post.presentation.dto.response;

import lombok.Builder;

@Builder
public class LikeUsersResponse {

    private String imageUrl;
    private String username;
    private Boolean following;

    private LikeUsersResponse() {
    }

    public LikeUsersResponse(String imageUrl, String username, Boolean following) {
        this.imageUrl = imageUrl;
        this.username = username;
        this.following = following;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getFollowing() {
        return following;
    }
}
