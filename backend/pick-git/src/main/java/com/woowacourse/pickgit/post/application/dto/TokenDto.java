package com.woowacourse.pickgit.post.application.dto;

public class TokenDto {

    private String accessToken;

    private TokenDto() {
    }

    public TokenDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
