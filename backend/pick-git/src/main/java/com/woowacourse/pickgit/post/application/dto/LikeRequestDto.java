package com.woowacourse.pickgit.post.application.dto;

public class LikeRequestDto {

    private String token;
    private Long postId;

    private LikeRequestDto() {
    }

    public LikeRequestDto(String token, Long postId) {
        this.token = token;
        this.postId = postId;
    }

    public String getToken() {
        return token;
    }

    public Long getPostId() {
        return postId;
    }
}
