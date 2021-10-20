package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import lombok.Builder;

@Builder
public class TagResponse {

    private String name;

    private TagResponse() {
    }

    public TagResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
