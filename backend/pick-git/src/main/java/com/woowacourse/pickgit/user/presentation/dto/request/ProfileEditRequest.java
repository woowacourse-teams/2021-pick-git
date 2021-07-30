package com.woowacourse.pickgit.user.presentation.dto.request;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class ProfileEditRequest {

    private MultipartFile image;
    private String description;

    private ProfileEditRequest() {
    }

    public ProfileEditRequest(
        @RequestParam(name = "image", required = false) MultipartFile image,
        @RequestParam(name = "description") String description) {
        this.image = image;
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}
