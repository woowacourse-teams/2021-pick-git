package com.woowacourse.pickgit.portfolio.application.dto.response;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.domain.section.Section;
import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import java.util.List;
import lombok.Builder;

@Builder
public class SectionResponseDto {

    private Long id;
    private String name;
    private List<ItemResponseDto> items;

    private SectionResponseDto() {
    }

    public SectionResponseDto(
        Long id,
        String name,
        List<ItemResponseDto> items
    ) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public static SectionResponseDto of(SectionRequest request) {
        return SectionResponseDto.builder()
            .id(request.getId())
            .name(request.getName())
            .items(createItemResponsesDtoFromItemRequests(request.getItems()))
            .build();
    }

    private static List<ItemResponseDto> createItemResponsesDtoFromItemRequests(
        List<ItemRequest> itemRequests
    ) {
        return itemRequests.stream()
            .map(ItemResponseDto::of)
            .collect(toList());
    }

    public static SectionResponseDto of(Section section) {
        return SectionResponseDto.builder()
            .id(section.getId())
            .name(section.getName())
            .items(createItemResponsesDtoFromItems(section.getItems()))
            .build();
    }

    private static List<ItemResponseDto> createItemResponsesDtoFromItems(
        List<Item> items
    ) {
        return items.stream()
            .map(ItemResponseDto::of)
            .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ItemResponseDto> getItems() {
        return items;
    }
}
