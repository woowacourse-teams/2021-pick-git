package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProfileImageEditRequestDto {

    private byte[] image;

    private ProfileImageEditRequestDto() {
    }

    public ProfileImageEditRequestDto(byte[] image) {
        this.image = image;
    }
}
