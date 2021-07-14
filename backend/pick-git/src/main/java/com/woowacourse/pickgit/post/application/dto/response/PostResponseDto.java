package com.woowacourse.pickgit.post.application.dto.response;

import java.util.List;

public class PostResponseDto {

    private Long id;
    private List<String> imageUrls;

    private PostResponseDto() {
    }

    public PostResponseDto(Long id) {
        this(id, null);
    }

    public PostResponseDto(Long id, List<String> imageUrls) {
        this.id = id;
        this.imageUrls = imageUrls;
    }

    public Long getId() {
        return id;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }
}
