package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class ItemResponse {

    private Long id;
    private String category;
    private List<DescriptionResponse> descriptions;

    private ItemResponse() {
    }

    public ItemResponse(
        Long id,
        String category,
        List<DescriptionResponse> descriptions
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

    public List<DescriptionResponse> getDescriptions() {
        return descriptions;
    }
}
