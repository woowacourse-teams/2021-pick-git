package com.woowacourse.pickgit.user.presentation.dto.request;

import javax.validation.constraints.NotNull;

public class ProfileDescriptionRequest {

    @NotNull
    private String description;

    private ProfileDescriptionRequest() {
    }

    public ProfileDescriptionRequest(String description) {
        this.description = description;
    }

    public String getDescription() {

        return description;
    }
}
