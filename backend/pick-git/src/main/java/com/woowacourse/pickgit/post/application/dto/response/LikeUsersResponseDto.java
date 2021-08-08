package com.woowacourse.pickgit.post.application.dto.response;

public class LikeUsersResponseDto {

    private String imageUrl;
    private String username;
    private Boolean following;

    private LikeUsersResponseDto() {
    }

    public LikeUsersResponseDto(String imageUrl, String username, Boolean following) {
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
