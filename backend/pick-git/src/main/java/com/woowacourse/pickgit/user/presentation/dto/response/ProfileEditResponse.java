package com.woowacourse.pickgit.user.presentation.dto.response;

public class ProfileEditResponse {

    private String imageUrl;
    private String description;

    private ProfileEditResponse() {
    }

    public ProfileEditResponse(String imageUrl, String description) {
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
