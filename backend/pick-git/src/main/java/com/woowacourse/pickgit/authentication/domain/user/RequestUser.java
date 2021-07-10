package com.woowacourse.pickgit.authentication.domain.user;

public abstract class RequestUser {
    private String username;
    private String accessToken;

    public RequestUser(String username, String accessToken) {
        this.username = username;
        this.accessToken = accessToken;
    }

    public abstract boolean isAnonymous();

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
