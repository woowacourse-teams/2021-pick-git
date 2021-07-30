package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import java.util.List;
import lombok.Builder;

@Builder
public class PostUpdateRequestDto {

    private Long postId;
    private List<String> tags;
    private String content;

    private PostUpdateRequestDto() {
    }

    public PostUpdateRequestDto(Long postId, List<String> tags, String content) {
        this.postId = postId;
        this.tags = tags;
        this.content = content;
    }

    public static PostUpdateRequestDto toUpdateRequestDto(
        Long postId,
        PostUpdateRequest updateRequest
    ) {
        return PostUpdateRequestDto.builder()
            .postId(postId)
            .tags(updateRequest.getTags())
            .content(updateRequest.getContent())
            .build();
    }

    public Long getPostId() {
        return postId;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }
}
