package com.woowacourse.pickgit.user.application.dto.response;

import lombok.Builder;

@Builder
public class ProfileEditResponseDto {

    private String imageUrl;
    private String description;

    public ProfileEditResponseDto() {
    }

    public ProfileEditResponseDto(String imageUrl, String description) {
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
