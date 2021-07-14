package com.woowacourse.pickgit.post.application.dto.response;

import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import java.util.List;

public class RepositoriesResponseDto {

    private List<RepositoryResponseDto> repositories;

    private RepositoriesResponseDto() {
    }

    public RepositoriesResponseDto(List<RepositoryResponseDto> repositories) {
        this.repositories = repositories;
    }

    public List<RepositoryResponseDto> showRepositories() {
        return repositories;
    }
}
