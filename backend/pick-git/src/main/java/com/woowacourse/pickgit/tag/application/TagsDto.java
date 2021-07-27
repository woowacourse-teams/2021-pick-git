package com.woowacourse.pickgit.tag.application;

import java.util.List;

public class TagsDto {

    private List<String> tagNames;

    private TagsDto() {
    }

    public TagsDto(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public List<String> getTagNames() {
        return tagNames;
    }
}
