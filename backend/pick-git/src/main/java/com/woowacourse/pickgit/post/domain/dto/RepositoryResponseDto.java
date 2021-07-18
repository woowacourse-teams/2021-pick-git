package com.woowacourse.pickgit.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryResponseDto {

    private String name;

    @JsonProperty("html_url")
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
