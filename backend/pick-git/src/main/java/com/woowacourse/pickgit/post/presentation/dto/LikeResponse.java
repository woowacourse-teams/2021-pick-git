package com.woowacourse.pickgit.post.presentation.dto;

public class LikeResponse {

    private Long likesCount;
    private boolean isLiked;

    private LikeResponse() {
    }

    public LikeResponse(Long likesCount, boolean isLiked) {
        this.likesCount = likesCount;
        this.isLiked = isLiked;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }
}
