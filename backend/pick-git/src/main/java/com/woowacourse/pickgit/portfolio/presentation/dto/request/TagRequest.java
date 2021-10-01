package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import lombok.Builder;

@Builder
public class TagRequest {

    private Long id;
    private String name;

    private TagRequest() {
    }

    public TagRequest(Long id, String name) {
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
