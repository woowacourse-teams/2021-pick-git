package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class SectionResponse {

    private Long id;
    private String name;
    private List<ItemResponse> items;

    private SectionResponse() {
    }

    public SectionResponse(
        Long id,
        String name,
        List<ItemResponse> items
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

    public List<ItemResponse> getItems() {
        return items;
    }
}
