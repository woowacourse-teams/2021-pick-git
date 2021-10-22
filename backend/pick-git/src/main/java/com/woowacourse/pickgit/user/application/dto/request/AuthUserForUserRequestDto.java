package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthUserForUserRequestDto {

    private String username;
    private String accessToken;
    private boolean isGuest;

    private AuthUserForUserRequestDto() {
    }

    private AuthUserForUserRequestDto(String username, boolean isGuest) {
        this(username, null, isGuest);
    }

    public AuthUserForUserRequestDto(String username, String accessToken, boolean isGuest) {
        this.username = username;
        this.accessToken = accessToken;
        this.isGuest = isGuest;
    }
}
