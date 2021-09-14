package com.woowacourse.pickgit.portfolio.application.dto.request;

import lombok.Builder;

@Builder
public class ContactRequestDto {

    private String category;
    private String value;

    private ContactRequestDto() {
    }

    public ContactRequestDto(String category, String value) {
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
