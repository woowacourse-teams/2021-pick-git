package com.woowacourse.pickgit.post.presentation.dto.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;

@Builder
public class PostUpdateRequest {

    private List<String> tags;

    @NotNull(message = "F0001")
    @Size(max = 500, message = "F0004")
    private String content;

    private PostUpdateRequest() {
    }

    public PostUpdateRequest(List<String> tags, String content) {
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
