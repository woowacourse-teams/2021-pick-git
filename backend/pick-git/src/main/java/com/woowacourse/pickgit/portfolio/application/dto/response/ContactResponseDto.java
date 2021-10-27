package com.woowacourse.pickgit.portfolio.application.dto.response;

import com.woowacourse.pickgit.portfolio.domain.contact.Contact;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import lombok.Builder;

@Builder
public class ContactResponseDto {

    private Long id;
    private String category;
    private String value;

    private ContactResponseDto() {
    }

    public ContactResponseDto(Long id, String category, String value) {
        this.id = id;
        this.category = category;
        this.value = value;
    }

    public static ContactResponseDto of(ContactRequest request) {
        return ContactResponseDto.builder()
            .id(request.getId())
            .category(request.getCategory())
            .value(request.getValue())
            .build();
    }

    public static ContactResponseDto of(Contact contact) {
        return ContactResponseDto.builder()
            .id(contact.getId())
            .category(contact.getCategory())
            .value(contact.getValue())
            .build();
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
