package com.woowacourse.pickgit.user.presentation.dto.request;

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
