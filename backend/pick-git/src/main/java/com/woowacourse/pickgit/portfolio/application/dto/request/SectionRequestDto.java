package com.woowacourse.pickgit.portfolio.application.dto.request;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import java.util.List;
import lombok.Builder;

@Builder
public class SectionRequestDto {

    private Long id;
    private String name;
    private List<ItemRequestDto> items;

    private SectionRequestDto() {
    }

    public SectionRequestDto(
        Long id,
        String name,
        List<ItemRequestDto> items
    ) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public static SectionRequestDto of(SectionRequest request) {
        return SectionRequestDto.builder()
            .id(request.getId())
            .name(request.getName())
            .items(createItemRequestsDto(request.getItems()))
            .build();
    }

    private static List<ItemRequestDto> createItemRequestsDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
            .map(ItemRequestDto::of)
            .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ItemRequestDto> getItems() {
        return items;
    }
}
