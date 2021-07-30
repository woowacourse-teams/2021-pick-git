package com.woowacourse.pickgit.post.presentation.dto.response;

import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import java.util.List;
import lombok.Builder;

@Builder
public class PostUpdateResponse {

    private List<String> tags;
    private String content;

    private PostUpdateResponse() {
    }

    public PostUpdateResponse(List<String> tags, String content) {
        this.tags = tags;
        this.content = content;
    }

    public static PostUpdateResponse toPostUpdateResponse(PostUpdateResponseDto updateResponseDto) {
        return PostUpdateResponse.builder()
            .tags(updateResponseDto.getTags())
            .content(updateResponseDto.getContent())
            .build();
    }

    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }
}
