package com.woowacourse.pickgit.portfolio.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class SectionResponseDto {

    private String name;
    private List<ItemResponseDto> items;

    private SectionResponseDto() {
    }

    public SectionResponseDto(
        String name,
        List<ItemResponseDto> items
    ) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<ItemResponseDto> getItems() {
        return items;
    }
}
