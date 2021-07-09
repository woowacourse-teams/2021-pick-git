package com.woowacourse.pickgit.tag.application;

public class ExtractionRequestDto {

    private final String accessToken;
    private final String userName;
    private final String repositoryName;

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
