package com.woowacourse.pickgit.authentication.presentation.dto;

public class ReissueAccessTokenResponse {

    private String accessToken;

    public ReissueAccessTokenResponse() {
    }

    public ReissueAccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
