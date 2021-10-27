package com.woowacourse.pickgit.authentication.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    private String scope;

    private String bearer;

    private OAuthAccessTokenResponse() {
    }

    public OAuthAccessTokenResponse(
        String accessToken,
        String tokenType,
        String scope,
        String bearer
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.bearer = bearer;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getScope() {
        return scope;
    }

    public String getBearer() {
        return bearer;
    }
}
