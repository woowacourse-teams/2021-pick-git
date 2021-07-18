package com.woowacourse.pickgit.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryResponseDto {

    private String name;

    @JsonProperty("html_url")
    private String htmlUrl;

    private RepositoryResponseDto() {
    }

    public RepositoryResponseDto(String name, String htmlUrl) {
        this.name = name;
        this.htmlUrl = htmlUrl;
    }

    public String getName() {
        return name;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }
}
