package com.woowacourse.pickgit.post.presentation.dto.response;

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

    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }
}
