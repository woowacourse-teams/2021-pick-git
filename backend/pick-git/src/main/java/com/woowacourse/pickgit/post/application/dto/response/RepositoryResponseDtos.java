package com.woowacourse.pickgit.post.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class RepositoryResponseDtos {

    private List<RepositoryResponseDto> repositoryResponseDtos;

    private RepositoryResponseDtos() {
    }

    public RepositoryResponseDtos(List<RepositoryResponseDto> repositoryResponseDtos) {
        this.repositoryResponseDtos = repositoryResponseDtos;
    }

    public List<RepositoryResponseDto> getRepositoryResponseDtos() {
        return repositoryResponseDtos;
    }
}
