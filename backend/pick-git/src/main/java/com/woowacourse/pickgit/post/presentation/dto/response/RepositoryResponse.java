package com.woowacourse.pickgit.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RepositoryResponse {

    @JsonProperty("html_url")
    private String url;
    
    private String name;

    private RepositoryResponse() {
    }

    public RepositoryResponse(String url, String name) {
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
