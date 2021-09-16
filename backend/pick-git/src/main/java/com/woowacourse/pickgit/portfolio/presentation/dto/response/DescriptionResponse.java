package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import lombok.Builder;

@Builder
public class DescriptionResponse {

    private Long id;
    private String value;

    private DescriptionResponse() {
    }

    public DescriptionResponse(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
