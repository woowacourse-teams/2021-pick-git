package com.woowacourse.pickgit.portfolio.application.dto.request;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import lombok.Builder;

@Builder
public class TagRequestDto {

    private Long id;
    private String name;

    private TagRequestDto() {
    }

    public TagRequestDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TagRequestDto of(TagRequest request) {
        return TagRequestDto.builder()
            .id(request.getId())
            .name(request.getName())
            .build();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
