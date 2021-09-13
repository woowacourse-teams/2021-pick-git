package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class ItemResponse {

    private String category;
    private List<String> descriptions;

    private ItemResponse() {
    }

    public ItemResponse(String category, List<String> descriptions) {
        this.category = category;
        this.descriptions = descriptions;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }
}
