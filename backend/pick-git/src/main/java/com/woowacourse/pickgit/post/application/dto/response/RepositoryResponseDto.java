package com.woowacourse.pickgit.post.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RepositoryResponseDto {

    @JsonProperty("html_url")
    private String url;

    private String name;

    private RepositoryResponseDto() {
    }

    public RepositoryResponseDto(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}
