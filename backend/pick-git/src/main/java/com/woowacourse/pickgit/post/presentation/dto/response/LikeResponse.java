package com.woowacourse.pickgit.post.presentation.dto.response;

public class LikeResponse {

    private int likesCount;
    private boolean liked;

    private LikeResponse() {
    }

    public LikeResponse(int likeCount, boolean liked) {
        this.likesCount = likeCount;
        this.liked = liked;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public boolean isLiked() {
        return liked;
    }
}
