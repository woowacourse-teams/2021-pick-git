package com.woowacourse.pickgit.post.domain.content;

import com.woowacourse.pickgit.exception.post.PostFormatException;
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
            throw new PostFormatException();
        }
    }

    private boolean isOver500(String content) {
        return content.length() > 500;
    }

    public String getContent() {
        return content;
    }
}
