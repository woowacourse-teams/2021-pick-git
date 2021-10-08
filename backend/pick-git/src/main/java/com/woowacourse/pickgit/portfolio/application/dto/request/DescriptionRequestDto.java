package com.woowacourse.pickgit.portfolio.application.dto.request;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import lombok.Builder;

@Builder
public class DescriptionRequestDto {

    private Long id;
    private String value;

    private DescriptionRequestDto() {
    }

    public DescriptionRequestDto(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public static DescriptionRequestDto of(DescriptionRequest request) {
        return DescriptionRequestDto.builder()
            .id(request.getId())
            .value(request.getValue())
            .build();
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
