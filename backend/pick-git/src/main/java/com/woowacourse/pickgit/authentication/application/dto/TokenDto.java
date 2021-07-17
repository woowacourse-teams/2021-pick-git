package com.woowacourse.pickgit.authentication.application.dto;

public class TokenDto {

    private String token;
    private String username;

    private TokenDto() {
    }

    public TokenDto(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
