package com.woowacourse.pickgit.comment.application.dto.request;

import lombok.Builder;

@Builder
public class QueryCommentRequestDto {
    private Long postId;
    private boolean isGuest;
    private int page;
    private int limit;

    private QueryCommentRequestDto() {

    }

    public QueryCommentRequestDto(Long postId, boolean isGuest, int page, int limit) {
        this.postId = postId;
        this.isGuest = isGuest;
        this.page = page;
        this.limit = limit;
    }

    public Long getPostId() {
        return postId;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }
}
