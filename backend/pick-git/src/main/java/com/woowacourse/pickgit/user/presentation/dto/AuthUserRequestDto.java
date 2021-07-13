package com.woowacourse.pickgit.user.presentation.dto;

public class AuthUserRequestDto {

    private String githubName;

    private AuthUserRequestDto() {
    }

    public AuthUserRequestDto(String githubName) {
        this.githubName = githubName;
    }

    public String getGithubName() {
        return githubName;
    }
}
