package com.woowacourse.pickgit.post.application.dto;

public class PostResponseDto {

    private Long id;

    private PostResponseDto() {
    }

    public PostResponseDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
