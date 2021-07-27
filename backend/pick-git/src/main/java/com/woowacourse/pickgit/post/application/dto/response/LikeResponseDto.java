package com.woowacourse.pickgit.post.application.dto.response;

public class LikeResponseDto {

    private int likeCount;
    private boolean isLiked;

    public LikeResponseDto(int likeCount, boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

    public LikeResponseDto() {
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }
}
