package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public class SectionRequest {

    private Long id;
    private String name;
    private List<ItemRequest> items;

    private SectionRequest() {
    }

    public SectionRequest(
        Long id,
        String name,
        List<ItemRequest> items
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

    public List<ItemRequest> getItems() {
        return items;
    }
}
