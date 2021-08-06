package com.woowacourse.pickgit.post.application.dto.request;

import lombok.Builder;

@Builder
public class RepositoryRequestDto {

    private String token;
    private String username;

    private RepositoryRequestDto() {
    }

    public RepositoryRequestDto(String token, String username) {
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
