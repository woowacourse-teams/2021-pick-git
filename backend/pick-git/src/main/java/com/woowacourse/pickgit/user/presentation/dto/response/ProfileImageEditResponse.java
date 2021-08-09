package com.woowacourse.pickgit.user.presentation.dto.response;

public class ProfileImageEditResponse {

    private String imageUrl;

    private ProfileImageEditResponse() {
    }

    public ProfileImageEditResponse(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
