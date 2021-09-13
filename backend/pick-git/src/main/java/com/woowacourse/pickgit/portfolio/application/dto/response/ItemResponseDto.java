package com.woowacourse.pickgit.portfolio.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class ItemResponseDto {

    private String category;
    private List<String> descriptions;

    private ItemResponseDto() {
    }

    public ItemResponseDto(String category, List<String> descriptions) {
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
