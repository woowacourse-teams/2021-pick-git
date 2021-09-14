package com.woowacourse.pickgit.portfolio.application.dto.request;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import java.util.List;
import lombok.Builder;

@Builder
public class ItemRequestDto {

    private Long id;
    private String category;
    private List<String> descriptions;

    private ItemRequestDto() {
    }

    public ItemRequestDto(Long id, String category, List<String> descriptions) {
        this.id = id;
        this.category = category;
        this.descriptions = descriptions;
    }

    public Long getId() {
        return id;
    }

    public static ItemRequestDto of(ItemRequest request) {
        return ItemRequestDto.builder()
            .id(request.getId())
            .category(request.getCategory())
            .descriptions(request.getDescriptions())
            .build();
    }

    public String getCategory() {
        return category;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }
}
