package com.woowacourse.pickgit.authentication.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthAccessTokenRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    private String code;

    public OAuthAccessTokenRequest() {
    }

    public OAuthAccessTokenRequest(String clientId, String clientSecret, String code) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.code = code;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCode() {
        return code;
    }
}
