package com.woowacourse.pickgit.post.presentation.dto.request;

public class SearchPostsRequest {

    private String type;
    private String keyword;

    private SearchPostsRequest() {
    }

    public SearchPostsRequest(String type, String keyword, int page, int limit) {
        this.type = type;
        this.keyword = keyword;
    }

    public String getType() {
        return type;
    }

    public String getKeyword() {
        return keyword;
    }
}
