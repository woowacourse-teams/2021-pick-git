package com.woowacourse.pickgit.tag.application;

import java.util.List;

public class TagsDto {

    private List<String> tags;

    private TagsDto() {
    }

    public TagsDto(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }
}
