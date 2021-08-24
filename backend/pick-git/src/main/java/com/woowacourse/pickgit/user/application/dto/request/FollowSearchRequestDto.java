package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;

@Builder
public class FollowSearchRequestDto {

    private String username;
    private Long page;
    private Long limit;

    private FollowSearchRequestDto() {
    }

    public FollowSearchRequestDto(String username, Long page, Long limit) {
        this.username = username;
        this.page = page;
        this.limit = limit;
    }

    public String getUsername() {
        return username;
    }

    public Long getPage() {
        return page;
    }

    public Long getLimit() {
        return limit;
    }
}
