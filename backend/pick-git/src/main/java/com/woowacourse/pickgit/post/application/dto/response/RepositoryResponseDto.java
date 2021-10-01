package com.woowacourse.pickgit.post.application.dto.response;

import lombok.Builder;

@Builder
public class RepositoryResponseDto {

    private String name;

    private String url;

    private RepositoryResponseDto() {
    }

    public RepositoryResponseDto(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
