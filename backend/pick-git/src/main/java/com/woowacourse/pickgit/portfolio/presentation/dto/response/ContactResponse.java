package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import lombok.Builder;

@Builder
public class ContactResponse {

    private Long id;
    private String category;
    private String value;

    private ContactResponse() {
    }

    public ContactResponse(Long id, String category, String value) {
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
