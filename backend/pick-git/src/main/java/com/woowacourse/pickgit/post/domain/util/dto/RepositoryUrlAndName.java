package com.woowacourse.pickgit.post.domain.util.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryUrlAndName {

    @JsonProperty("html_url")
    private String url;

    private String name;

    private RepositoryUrlAndName() {
    }

    public RepositoryUrlAndName(String name, String url) {
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
