package com.woowacourse.pickgit.portfolio.application.dto.response;

import com.woowacourse.pickgit.portfolio.domain.section.item.Description;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import lombok.Builder;

@Builder
public class DescriptionResponseDto {

    private Long id;
    private String value;

    private DescriptionResponseDto() {
    }

    public DescriptionResponseDto(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public static DescriptionResponseDto of(DescriptionRequest request) {
        return DescriptionResponseDto.builder()
            .id(request.getId())
            .value(request.getValue())
            .build();
    }

    public static DescriptionResponseDto of(Description description) {
        return DescriptionResponseDto.builder()
            .id(description.getId())
            .value(description.getValue())
            .build();
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
