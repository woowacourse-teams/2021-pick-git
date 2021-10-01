package com.woowacourse.pickgit.post.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
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
