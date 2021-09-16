package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import lombok.Builder;

@Builder
public class ContactRequest {

    private Long id;
    private String category;
    private String value;

    private ContactRequest() {
    }

    public ContactRequest(Long id, String category, String value) {
        this.id = id;
        this.category = category;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        return value;
    }
}
