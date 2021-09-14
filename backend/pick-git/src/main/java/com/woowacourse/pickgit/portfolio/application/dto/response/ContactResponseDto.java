package com.woowacourse.pickgit.portfolio.application.dto.response;

import lombok.Builder;

@Builder
public class ContactResponseDto {

    private String category;
    private String value;

    private ContactResponseDto() {
    }

    public ContactResponseDto(String category, String value) {
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
