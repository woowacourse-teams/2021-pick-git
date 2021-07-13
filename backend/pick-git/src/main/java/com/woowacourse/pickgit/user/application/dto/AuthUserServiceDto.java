package com.woowacourse.pickgit.user.application.dto;

public class AuthUserServiceDto {

    private String githubName;

    public AuthUserServiceDto(String githubName) {
        this.githubName = githubName;
    }

    public String getGithubName() {
        return githubName;
    }
}
