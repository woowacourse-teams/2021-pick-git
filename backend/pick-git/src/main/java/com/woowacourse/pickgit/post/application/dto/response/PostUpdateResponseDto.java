package com.woowacourse.pickgit.post.application.dto.response;

import com.woowacourse.pickgit.post.domain.Post;
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

    public static PostUpdateResponseDto toPostUpdateResponseDto(Post post) {
        return PostUpdateResponseDto.builder()
            .content(post.getContent())
            .tags(post.getTagNames())
            .build();
    }

    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }
}
