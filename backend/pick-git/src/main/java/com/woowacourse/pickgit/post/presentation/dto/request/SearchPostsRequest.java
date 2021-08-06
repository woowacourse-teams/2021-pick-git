package com.woowacourse.pickgit.post.presentation.dto.request;

public class SearchPostsRequest {

    private String type;
    private String keyword;
    private int page;
    private int limit;

    private SearchPostsRequest() {
    }

    public SearchPostsRequest(String type, String keyword, int page, int limit) {
        this.type = type;
        this.keyword = keyword;
        this.page = page;
        this.limit = limit;
    }

    public String getType() {
        return type;
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
