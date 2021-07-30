package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public class ProfileEditRequestDto {

    private MultipartFile image;
    private String decription;

    private ProfileEditRequestDto() {
    }

    public ProfileEditRequestDto(MultipartFile image, String decription) {
        this.image = image;
        this.decription = decription;
    }

    public MultipartFile getImage() {
        return image;
    }

    public String getImageOriginalFileName() {
        return image.getOriginalFilename();
    }

    public String getDecription() {
        return decription;
    }
}
