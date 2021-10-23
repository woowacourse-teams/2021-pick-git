package com.woowacourse.pickgit.post.presentation.dto.response;

import lombok.Builder;

@Builder
public class LikeResponse {

    private int likesCount;
    private Boolean liked;

    private LikeResponse() {
    }

    public LikeResponse(int likeCount, boolean liked) {
        this.likesCount = likeCount;
        this.liked = liked;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public Boolean getLiked() {
        return liked;
    }
}
