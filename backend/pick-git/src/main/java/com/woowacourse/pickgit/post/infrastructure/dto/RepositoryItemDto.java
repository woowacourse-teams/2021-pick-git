package com.woowacourse.pickgit.post.infrastructure.dto;

import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import java.util.List;

public class RepositoryItemDto {

    private List<RepositoryNameAndUrl> items;

    private RepositoryItemDto() {
    }

    public RepositoryItemDto(List<RepositoryNameAndUrl> items) {
        this.items = items;
    }

    public List<RepositoryNameAndUrl> getItems() {
        return items;
    }
}
