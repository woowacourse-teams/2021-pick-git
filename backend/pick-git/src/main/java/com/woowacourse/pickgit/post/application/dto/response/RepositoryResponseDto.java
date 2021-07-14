package com.woowacourse.pickgit.post.application.dto.response;

import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import java.util.List;

public class RepositoryResponseDto {

    private List<RepositoryResponse> repositories;

    private RepositoryResponseDto() {
    }

    public RepositoryResponseDto(List<RepositoryResponse> repositories) {
        this.repositories = repositories;
    }

    public List<RepositoryResponse> showRepositories() {
        return repositories;
    }
}
