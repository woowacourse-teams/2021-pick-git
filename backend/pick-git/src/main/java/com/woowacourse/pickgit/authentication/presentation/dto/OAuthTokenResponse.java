package com.woowacourse.pickgit.authentication.presentation.dto;

public class OAuthTokenResponse {

    private String token;

    public OAuthTokenResponse() {
    }

    public OAuthTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
