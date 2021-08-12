package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthUserForPostRequestDto {

    private String username;
    private boolean isGuest;

    private AuthUserForPostRequestDto() {
    }

    public AuthUserForPostRequestDto(String username, boolean isGuest) {
        this.username = username;
        this.isGuest = isGuest;
    }
}

