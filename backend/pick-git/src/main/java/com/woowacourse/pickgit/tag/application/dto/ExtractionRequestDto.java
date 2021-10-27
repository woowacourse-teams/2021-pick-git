package com.woowacourse.pickgit.tag.application.dto;

import lombok.Builder;

@Builder
public class ExtractionRequestDto {

    private String accessToken;
    private String userName;
    private String repositoryName;

    private ExtractionRequestDto() {
    }

    public ExtractionRequestDto(String accessToken, String userName, String repositoryName) {
        this.accessToken = accessToken;
        this.userName = userName;
        this.repositoryName = repositoryName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }
}
