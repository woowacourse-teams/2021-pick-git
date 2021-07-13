package com.woowacourse.pickgit.post.domain;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class PostContent {

    @Lob
    private String content;

    protected PostContent() {
    }

    public PostContent(String content) {
        validate(content);
        this.content = content;
    }

    private void validate(String content) {
        if (isOver500(content)) {
            throw new IllegalArgumentException("F0001");
        }
    }

    private boolean isOver500(String content) {
        return content.length() > 500;
    }
}
