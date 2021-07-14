package com.woowacourse.pickgit.post.domain.comment;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CommentContent {

    private static final int MAX_COMMENT_CONTENT_LENGTH = 100;

    @Column(nullable = false, length = 100)
    @Lob
    private String content;

    protected CommentContent() {
    }

    public CommentContent(String content) {
        if (isNotValidContent(content)) {
            throw new CommentFormatException();
        }
        this.content = content;
    }

    private boolean isNotValidContent(String content) {
        return Objects.isNull(content)
            || content.isEmpty()
            || content.length() > MAX_COMMENT_CONTENT_LENGTH;
    }

    public String getContent() {
        return content;
    }
}
