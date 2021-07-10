package com.woowacourse.pickgit.post.application.dto;

public class LikeResponseDto {

    private Long likesCount;
    private boolean isLiked;

    private LikeResponseDto() {
    }

    public LikeResponseDto(Long likesCount, boolean isLiked) {
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
