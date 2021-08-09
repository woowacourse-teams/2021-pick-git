package com.woowacourse.pickgit.user.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProfileImageEditResponseDto {

    private String imageUrl;

    private ProfileImageEditResponseDto() {
    }

    public ProfileImageEditResponseDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
