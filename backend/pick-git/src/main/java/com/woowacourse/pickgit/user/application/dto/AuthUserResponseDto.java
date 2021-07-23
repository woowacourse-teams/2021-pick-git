package com.woowacourse.pickgit.user.application.dto;

public class AuthUserResponseDto {

    private String githubName;

    public AuthUserResponseDto(String githubName) {
        this.githubName = githubName;
    }

    public String getGithubName() {
        return githubName;
    }
}
