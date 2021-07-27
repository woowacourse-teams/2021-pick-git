package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthUserRequestDto {

    private String githubName;
    private boolean isGuest;

    private AuthUserRequestDto() {
    }

    public AuthUserRequestDto(String githubName) {
        this.githubName = githubName;
    }

    public AuthUserRequestDto(String githubName, boolean isGuest) {
        this.githubName = githubName;
        this.isGuest = isGuest;
    }
}
