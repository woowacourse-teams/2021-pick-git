package com.woowacourse.pickgit.post.application.dto.request;

public class SearchRepositoryRequestDto {

    private String token;
    private String username;
    private String keyword;
    private int page;
    private int limit;

    private SearchRepositoryRequestDto() {
    }

    public SearchRepositoryRequestDto(
        String token,
        String username,
        String keyword,
        int page,
        int limit
    ) {
        this.token = token;
        this.username = username;
        this.keyword = keyword;
        this.page = page;
        this.limit = limit;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }
}
