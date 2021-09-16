package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public class ItemRequest {

    private Long id;
    private String category;
    private List<DescriptionRequest> descriptions;

    private ItemRequest() {
    }

    public ItemRequest(
        Long id,
        String category,
        List<DescriptionRequest> descriptions
    ) {
        this.id = id;
        this.category = category;
        this.descriptions = descriptions;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public List<DescriptionRequest> getDescriptions() {
        return descriptions;
    }
}
