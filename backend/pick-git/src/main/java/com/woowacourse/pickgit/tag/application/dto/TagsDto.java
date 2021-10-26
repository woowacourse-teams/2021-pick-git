package com.woowacourse.pickgit.tag.application.dto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TagsDto {

    private List<String> tagNames;

    private TagsDto() {
    }

    public TagsDto(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public List<String> getTagNames() {
        return Optional.ofNullable(tagNames)
            .orElseGet(Collections::emptyList);
    }
}
