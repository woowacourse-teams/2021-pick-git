package com.woowacourse.pickgit.user.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthUserRequestDto {

    private String username;
    private String accessToken;
    private boolean isGuest;

    private AuthUserRequestDto() {
    }

    private AuthUserRequestDto(String username, boolean isGuest) {
        this(username, null, isGuest);
    }

    public AuthUserRequestDto(String username, String accessToken, boolean isGuest) {
        this.username = username;
        this.accessToken = accessToken;
        this.isGuest = isGuest;
    }

    public static AuthUserRequestDto from(AppUser appUser) {
        if (appUser.isGuest()) {
            return new AuthUserRequestDto(null, true);
        }
        return new AuthUserRequestDto(appUser.getUsername(), appUser.getAccessToken(), false);
    }
}
