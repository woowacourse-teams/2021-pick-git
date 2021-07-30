package com.woowacourse.pickgit.post.application.dto.response;

import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import java.util.List;
import lombok.Builder;

@Builder
public class PostUpdateResponseDto {

    private List<String> tags;
    private String content;

    private PostUpdateResponseDto() {
    }

    public PostUpdateResponseDto(List<String> tags, String content) {
        this.tags = tags;
        this.content = content;
    }

    public static PostUpdateResponseDto toPostUpdateResponseDto(
        PostUpdateRequestDto updateRequestDto)
    {
        return PostUpdateResponseDto.builder()
            .content(updateRequestDto.getContent())
            .tags(updateRequestDto.getTags())
            .build();
    }

    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }
}
