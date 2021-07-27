package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;

@Builder
public class ContributionRequestDto {

    private String username;

    private ContributionRequestDto() {
    }

    public ContributionRequestDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
