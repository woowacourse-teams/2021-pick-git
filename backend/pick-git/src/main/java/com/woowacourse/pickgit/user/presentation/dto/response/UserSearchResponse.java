package com.woowacourse.pickgit.user.presentation.dto.response;

import lombok.Builder;

@Builder
public class UserSearchResponse {

    private String imageUrl;
    private String username;
    private Boolean following;

    private UserSearchResponse() {
    }

    public UserSearchResponse(String imageUrl, String username, Boolean following) {
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
