package com.woowacourse.pickgit.portfolio.application.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public class SectionRequestDto {

    private String name;
    private List<ItemRequestDto> items;

    private SectionRequestDto() {
    }

    public SectionRequestDto(
        String name,
        List<ItemRequestDto> items
    ) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<ItemRequestDto> getItems() {
        return items;
    }
}
