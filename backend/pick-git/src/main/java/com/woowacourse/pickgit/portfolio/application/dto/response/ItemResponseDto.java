package com.woowacourse.pickgit.portfolio.application.dto.response;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.domain.section.item.Description;
import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import java.util.List;
import lombok.Builder;

@Builder
public class ItemResponseDto {

    private Long id;
    private String category;
    private List<DescriptionResponseDto> descriptions;

    private ItemResponseDto() {
    }

    public ItemResponseDto(
        Long id,
        String category,
        List<DescriptionResponseDto> descriptions
    ) {
        this.id = id;
        this.category = category;
        this.descriptions = descriptions;
    }

    public static ItemResponseDto of(ItemRequest request) {
        return ItemResponseDto.builder()
            .id(request.getId())
            .category(request.getCategory())
            .descriptions(getDescriptionResponsesDtoFromDescriptionRequests(
                request.getDescriptions()
            ))
            .build();
    }

    private static List<DescriptionResponseDto> getDescriptionResponsesDtoFromDescriptionRequests(
        List<DescriptionRequest> requests
    ) {
        return requests.stream()
            .map(DescriptionResponseDto::of)
            .collect(toList());
    }

    public static ItemResponseDto of(Item item) {
        return ItemResponseDto.builder()
            .id(item.getId())
            .category(item.getCategory())
            .descriptions(getDescriptionResponsesDtoFromDescriptions(
                item.getDescriptions()
            ))
            .build();
    }

    private static List<DescriptionResponseDto> getDescriptionResponsesDtoFromDescriptions(
        List<Description> descriptions
    ) {
        return descriptions.stream()
            .map(DescriptionResponseDto::of)
            .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public List<DescriptionResponseDto> getDescriptions() {
        return descriptions;
    }
}
