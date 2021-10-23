package com.woowacourse.pickgit.post.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class RepositoryResponsesDto {


    private List<RepositoryResponseDto> repositoryResponsesDto;

    private RepositoryResponsesDto() {
    }

    public RepositoryResponsesDto(List<RepositoryResponseDto> repositoryResponsesDto) {
        this.repositoryResponsesDto = repositoryResponsesDto;
    }

    public List<RepositoryResponseDto> getRepositoryResponsesDto() {
        return repositoryResponsesDto;
    }
}
