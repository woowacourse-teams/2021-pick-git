package com.woowacourse.pickgit.post.presentation.dto.response;

public class LikeResponse {

    private int likeCount;
    private boolean liked;

    private LikeResponse() {
    }

    public LikeResponse(int likeCount, boolean liked) {
        this.likeCount = likeCount;
        this.liked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return liked;
    }
}
