package com.woowacourse.pickgit.comment.application.dto.request;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public class QueryCommentRequestDto {

    private Long postId;
    private boolean isGuest;
    private Pageable pageable;

    private QueryCommentRequestDto() {

    }

    public QueryCommentRequestDto(Long postId, boolean isGuest, Pageable pageable) {
        this.postId = postId;
        this.isGuest = isGuest;
        this.pageable = pageable;
    }

    public Long getPostId() {
        return postId;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public Pageable getPageable() {
        return pageable;
    }
}
