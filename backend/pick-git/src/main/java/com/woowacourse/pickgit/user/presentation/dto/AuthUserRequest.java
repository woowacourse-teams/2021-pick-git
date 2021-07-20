package com.woowacourse.pickgit.user.presentation.dto;

public class AuthUserRequest {

    private String githubName;

    private AuthUserRequest() {
    }

    public AuthUserRequest(String githubName) {
        this.githubName = githubName;
    }

    public String getGithubName() {
        return githubName;
    }
}
