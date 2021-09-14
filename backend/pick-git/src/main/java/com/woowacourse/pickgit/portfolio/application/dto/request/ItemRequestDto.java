package com.woowacourse.pickgit.portfolio.application.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public class ItemRequestDto {

    private String category;
    private List<String> descriptions;

    private ItemRequestDto() {
    }

    public ItemRequestDto(String category, List<String> descriptions) {
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
