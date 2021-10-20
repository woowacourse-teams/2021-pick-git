package com.woowacourse.pickgit.portfolio.application.dto.response;

import com.woowacourse.pickgit.portfolio.domain.project.ProjectTag;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import lombok.Builder;

@Builder
public class TagResponseDto {

    private String name;

    private TagResponseDto() {
    }

    public TagResponseDto(String name) {
        this.name = name;
    }

    public static TagResponseDto of(TagRequest request) {
        return TagResponseDto.builder()
            .name(request.getName())
            .build();
    }

    public static TagResponseDto of(ProjectTag tag) {
        return TagResponseDto.builder()
            .name(tag.getTagName())
            .build();
    }

    public String getName() {
        return name;
    }
}
