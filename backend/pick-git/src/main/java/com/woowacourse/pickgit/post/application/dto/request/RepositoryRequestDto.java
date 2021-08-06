package com.woowacourse.pickgit.post.application.dto.request;

import lombok.Builder;

@Builder
public class RepositoryRequestDto {

    private String token;
    private String username;
    private Long page;
    private Long limit;

    private RepositoryRequestDto() {
    }

    public RepositoryRequestDto(String token, String username, Long page, Long limit) {
        this.token = token;
        this.username = username;
        this.page = page;
        this.limit = limit;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Long getPage() {
        return page;
    }

    public Long getLimit() {
        return limit;
    }
}
