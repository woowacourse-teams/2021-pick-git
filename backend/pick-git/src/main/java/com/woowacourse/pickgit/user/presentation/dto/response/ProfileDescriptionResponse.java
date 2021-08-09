package com.woowacourse.pickgit.user.presentation.dto.response;

public class ProfileDescriptionResponse {

    private String description;

    private ProfileDescriptionResponse() {
    }

    public ProfileDescriptionResponse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
