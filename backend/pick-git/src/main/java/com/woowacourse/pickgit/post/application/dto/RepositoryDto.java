package com.woowacourse.pickgit.post.application.dto;

import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import java.util.List;

public class RepositoryDto {

    private List<RepositoryResponse> repositories;

    private RepositoryDto() {
    }

    public RepositoryDto(List<RepositoryResponse> repositories) {
        this.repositories = repositories;
    }

    public List<RepositoryResponse> getRepositories() {
        return repositories;
    }
}
