package com.woowacourse.pickgit.authentication.presentation.dto;

public class OAuthTokenResponse {

    private String token;
    private String username;

    private OAuthTokenResponse() {
    }

    public OAuthTokenResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
