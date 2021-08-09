package com.woowacourse.pickgit.user.presentation.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProfileDescriptionRequest {

    @NotNull
    @Size(max = 160)
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
