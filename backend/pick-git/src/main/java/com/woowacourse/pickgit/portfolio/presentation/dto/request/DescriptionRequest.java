package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import lombok.Builder;

@Builder
public class DescriptionRequest {

    private Long id;
    private String value;

    private DescriptionRequest() {
    }

    public DescriptionRequest(Long id, String value) {
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
