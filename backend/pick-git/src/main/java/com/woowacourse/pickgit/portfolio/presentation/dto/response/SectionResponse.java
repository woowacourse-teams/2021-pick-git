package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class SectionResponse {

    private String name;
    private List<ItemResponse> items;

    private SectionResponse() {
    }

    public SectionResponse(
        String name,
        List<ItemResponse> items
    ) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<ItemResponse> getItems() {
        return items;
    }
}
