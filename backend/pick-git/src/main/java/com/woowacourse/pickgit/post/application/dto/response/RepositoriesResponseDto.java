package com.woowacourse.pickgit.post.application.dto.response;

import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import java.util.List;

public class RepositoriesResponseDto {

    private List<RepositoryResponse> repositories;

    private RepositoriesResponseDto() {
    }

    public RepositoriesResponseDto(List<RepositoryResponse> repositories) {
        this.repositories = repositories;
    }

    public List<RepositoryResponse> showRepositories() {
        return repositories;
    }
}
