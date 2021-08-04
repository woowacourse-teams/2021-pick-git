package com.woowacourse.pickgit.user.application.dto.request;

import lombok.Builder;

@Builder
public class UserSearchRequestDto {

    private String keyword;
    private Long page;
    private Long limit;

    private UserSearchRequestDto() {
    }

    public UserSearchRequestDto(String keyword, Long page, Long limit) {
        this.keyword = keyword;
        this.page = page;
        this.limit = limit;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getPage() {
        return Math.toIntExact(page);
    }

    public int getLimit() {
        return Math.toIntExact(limit);
    }
}
