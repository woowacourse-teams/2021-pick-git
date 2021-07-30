package com.woowacourse.pickgit.user.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthUserRequestDto {

    private String githubName;
    private boolean isGuest;

    private AuthUserRequestDto() {
    }

    public AuthUserRequestDto(String githubName, boolean isGuest) {
        this.githubName = githubName;
        this.isGuest = isGuest;
    }

    public static AuthUserRequestDto from(AppUser appUser) {
        if (appUser.isGuest()) {
            return new AuthUserRequestDto(null, true);
        }
        return new AuthUserRequestDto(appUser.getUsername(), false);
    }
}
