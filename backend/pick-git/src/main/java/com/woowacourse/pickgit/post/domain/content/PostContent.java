package com.woowacourse.pickgit.post.domain.content;

import com.woowacourse.pickgit.exception.post.PostFormatException;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class PostContent {

    public static final int MAXIMUM_CONTENT_LENGTH = 500;

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
        return content.length() > MAXIMUM_CONTENT_LENGTH;
    }

    public String getContent() {
        return content;
    }
}
