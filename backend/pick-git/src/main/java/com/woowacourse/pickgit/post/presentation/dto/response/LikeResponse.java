package com.woowacourse.pickgit.post.presentation.dto.response;

public class LikeResponse {
    private int likeCount;
    private boolean isLiked;

    public LikeResponse(int likeCount, boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

    public LikeResponse() {
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }
}
