package com.woowacourse.pickgit.post.application.dto.request;

public class TokenRequestDto {

    private String accessToken;

    private TokenRequestDto() {
    }

    public TokenRequestDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
