package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import lombok.Builder;

@Builder
public class TagRequest {

    private String name;

    private TagRequest() {
    }

    public TagRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
