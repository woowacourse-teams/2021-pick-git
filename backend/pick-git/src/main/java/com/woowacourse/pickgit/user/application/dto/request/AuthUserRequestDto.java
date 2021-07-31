package com.woowacourse.pickgit.user.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthUserRequestDto {

    private String username;
    private boolean isGuest;

    private AuthUserRequestDto() {
    }

    private AuthUserRequestDto(String username, boolean isGuest) {
        this.username = username;
        this.isGuest = isGuest;
    }

    public static AuthUserRequestDto from(AppUser appUser) {
        if (appUser.isGuest()) {
            return new AuthUserRequestDto(null, true);
        }
        return new AuthUserRequestDto(appUser.getUsername(), false);
    }
}
