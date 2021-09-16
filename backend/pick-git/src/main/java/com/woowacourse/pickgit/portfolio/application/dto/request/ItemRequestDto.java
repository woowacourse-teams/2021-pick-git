package com.woowacourse.pickgit.portfolio.application.dto.request;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import java.util.List;
import lombok.Builder;

@Builder
public class ItemRequestDto {

    private Long id;
    private String category;
    private List<DescriptionRequestDto> descriptions;

    private ItemRequestDto() {
    }

    public ItemRequestDto(
        Long id,
        String category,
        List<DescriptionRequestDto> descriptions
    ) {
        this.id = id;
        this.category = category;
        this.descriptions = descriptions;
    }

    public static ItemRequestDto of(ItemRequest request) {
        return ItemRequestDto.builder()
            .id(request.getId())
            .category(request.getCategory())
            .descriptions(getDescriptionRequestsDto(request.getDescriptions()))
            .build();
    }

    private static List<DescriptionRequestDto> getDescriptionRequestsDto(
        List<DescriptionRequest> requests
    ) {
        return requests.stream()
            .map(DescriptionRequestDto::of)
            .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public List<DescriptionRequestDto> getDescriptions() {
        return descriptions;
    }
}
