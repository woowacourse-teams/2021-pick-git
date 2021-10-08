package com.woowacourse.pickgit.portfolio.application.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public class SectionRequestDto {

    private Long id;
    private String name;
    private List<ItemRequestDto> items;

    private SectionRequestDto() {
    }

    public SectionRequestDto(
        Long id,
        String name,
        List<ItemRequestDto> items
    ) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ItemRequestDto> getItems() {
        return items;
    }
}
