package com.woowacourse.pickgit.post.presentation.dto.response;

public class LikeResponse {
    private int likeCount;
    private boolean liked;

    public LikeResponse(int likeCount, boolean liked) {
        this.likeCount = likeCount;
        this.liked = liked;
    }

    public LikeResponse() {
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return liked;
    }
}
