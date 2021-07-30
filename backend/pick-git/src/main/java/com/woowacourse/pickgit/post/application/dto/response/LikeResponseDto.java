package com.woowacourse.pickgit.post.application.dto.response;

public class LikeResponseDto {

    private int likesCount;
    private Boolean liked;

    private LikeResponseDto() {
    }

    public LikeResponseDto(int likesCount, boolean liked) {
        this.likesCount = likesCount;
        this.liked = liked;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public Boolean getLiked() {
        return liked;
    }
}
