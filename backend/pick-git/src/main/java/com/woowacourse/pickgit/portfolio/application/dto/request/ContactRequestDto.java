package com.woowacourse.pickgit.portfolio.application.dto.request;

import lombok.Builder;

@Builder
public class ContactRequestDto {

    private Long id;
    private String category;
    private String value;

    private ContactRequestDto() {
    }

    public ContactRequestDto(Long id, String category, String value) {
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
