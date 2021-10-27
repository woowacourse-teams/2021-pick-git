package com.woowacourse.pickgit.authentication.presentation.dto;

public class OAuthLoginUrlResponse {

    private String url;

    private OAuthLoginUrlResponse() {
    }

    public OAuthLoginUrlResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
