package com.woowacourse.pickgit.post.application.dto.request;

import lombok.Builder;

@Builder
public class PostDeleteRequestDto {

    private Long postId;

    private PostDeleteRequestDto() {
    }

    public static PostDeleteRequestDto toPostDeleteRequestDto(Long postId) {
        return PostDeleteRequestDto.builder()
            .postId(postId)
            .build();
    }

    public PostDeleteRequestDto(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }
}
