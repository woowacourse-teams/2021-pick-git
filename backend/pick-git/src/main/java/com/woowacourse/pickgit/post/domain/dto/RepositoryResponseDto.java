package com.woowacourse.pickgit.post.domain.dto;

public class RepositoryResponseDto {

    private String name;

    private RepositoryResponseDto() {
    }

    public RepositoryResponseDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
