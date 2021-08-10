package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowRequestDto {

    private AuthUserForUserRequestDto authUserRequestDto;
    private String targetName;
    private boolean githubFollowing;

    private FollowRequestDto() {
    }

    public FollowRequestDto(
        AuthUserForUserRequestDto authUserRequestDto,
        String targetName,
        boolean githubFollowing
    ) {
        this.authUserRequestDto = authUserRequestDto;
        this.targetName = targetName;
        this.githubFollowing = githubFollowing;
    }

    public String getAccessToken() {
        return this.authUserRequestDto.getAccessToken();
    }
}
