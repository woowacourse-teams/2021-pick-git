package com.woowacourse.pickgit.post.infrastructure.dto;

public class RepositoryResponse {

    private String name;

    private RepositoryResponse() {
    }

    public RepositoryResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
