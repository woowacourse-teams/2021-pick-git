package com.woowacourse.pickgit.portfolio.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;

@Builder
public class UserDto {

    private String username;
    private String accessToken;
    private boolean isGuest;

    private UserDto() {
    }

    public UserDto(String username, String accessToken, boolean isGuest) {
        this.username = username;
        this.accessToken = accessToken;
        this.isGuest = isGuest;
    }

    public static UserDto from(AppUser user) {
        if (user.isGuest()) {
            return new UserDto(null, null, true);
        }
        return new UserDto(user.getUsername(), user.getAccessToken(), false);
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isGuest() {
        return isGuest;
    }
}
