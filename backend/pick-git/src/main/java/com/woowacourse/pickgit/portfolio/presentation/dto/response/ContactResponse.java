package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import lombok.Builder;

@Builder
public class ContactResponse {

    private String category;
    private String value;

    private ContactResponse() {
    }

    public ContactResponse(String category, String value) {
        this.category = category;
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        return value;
    }
}
