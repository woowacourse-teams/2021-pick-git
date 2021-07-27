package com.woowacourse.pickgit.post.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class PostImageUrlResponseDto {

    private Long id;
    private List<String> imageUrls;

    private PostImageUrlResponseDto() {
    }

    public PostImageUrlResponseDto(Long id) {
        this(id, List.of());
    }

    public PostImageUrlResponseDto(Long id, List<String> imageUrls) {
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
