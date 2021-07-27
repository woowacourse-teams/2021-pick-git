package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthUserRequestDto {

    private String githubName;

    private AuthUserRequestDto() {
    }

    public AuthUserRequestDto(String githubName) {
        this.githubName = githubName;
    }
}
