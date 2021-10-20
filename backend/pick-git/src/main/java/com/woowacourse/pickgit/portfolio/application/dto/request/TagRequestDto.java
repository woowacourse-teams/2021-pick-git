package com.woowacourse.pickgit.portfolio.application.dto.request;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import lombok.Builder;

@Builder
public class TagRequestDto {

    private String name;

    private TagRequestDto() {
    }

    public TagRequestDto(String name) {
        this.name = name;
    }

    public static TagRequestDto of(TagRequest request) {
        return TagRequestDto.builder()
            .name(request.getName())
            .build();
    }

    public String getName() {
        return name;
    }
}
