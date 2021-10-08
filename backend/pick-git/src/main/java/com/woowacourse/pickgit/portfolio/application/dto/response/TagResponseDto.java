package com.woowacourse.pickgit.portfolio.application.dto.response;

import com.woowacourse.pickgit.portfolio.domain.project.ProjectTag;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import lombok.Builder;

@Builder
public class TagResponseDto {

    private Long id;
    private String name;

    private TagResponseDto() {
    }

    public TagResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TagResponseDto of(TagRequest request) {
        return TagResponseDto.builder()
            .id(request.getId())
            .name(request.getName())
            .build();
    }

    public static TagResponseDto of(ProjectTag tag) {
        return TagResponseDto.builder()
            .id(tag.getId())
            .name(tag.getTagName())
            .build();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
