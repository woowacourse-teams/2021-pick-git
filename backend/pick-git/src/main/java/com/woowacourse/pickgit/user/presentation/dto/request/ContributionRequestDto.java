package com.woowacourse.pickgit.user.presentation.dto.request;

import lombok.Builder;

@Builder
public class ContributionRequestDto {

    private String accessToken;
    private String username;

    private ContributionRequestDto() {
    }

    public ContributionRequestDto(String accessToken, String username) {
        this.accessToken = accessToken;
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUsername() {
        return username;
    }
}
