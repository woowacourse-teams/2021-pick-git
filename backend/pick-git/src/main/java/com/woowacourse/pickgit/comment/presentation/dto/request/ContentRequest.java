package com.woowacourse.pickgit.comment.presentation.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ContentRequest {

    @NotBlank(message = "F0001")
    @Size(max = 100, message = "F0002")
    private String content;

    private ContentRequest() {
    }

    public ContentRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
