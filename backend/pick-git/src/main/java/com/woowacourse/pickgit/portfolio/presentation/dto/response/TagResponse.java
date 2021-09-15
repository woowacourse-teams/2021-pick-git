package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import lombok.Builder;

@Builder
public class TagResponse {

    private Long id;
    private String name;

    private TagResponse() {
    }

    public TagResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
