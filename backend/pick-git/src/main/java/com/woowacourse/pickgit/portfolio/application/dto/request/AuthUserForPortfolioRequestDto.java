package com.woowacourse.pickgit.portfolio.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;

@Builder
public class AuthUserForPortfolioRequestDto {

    private String username;
    private String accessToken;
    private boolean isGuest;

    private AuthUserForPortfolioRequestDto() {
    }

    public AuthUserForPortfolioRequestDto(String username, String accessToken, boolean isGuest) {
        this.username = username;
        this.accessToken = accessToken;
        this.isGuest = isGuest;
    }

    public static AuthUserForPortfolioRequestDto from(AppUser user) {
        if (user.isGuest()) {
            return new AuthUserForPortfolioRequestDto(null, null, false);
        }
        return new AuthUserForPortfolioRequestDto(user.getUsername(), user.getAccessToken(), true);
    }

    public boolean isGuest() {
        return isGuest;
    }
}
