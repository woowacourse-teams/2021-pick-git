package com.woowacourse.pickgit.post.domain.content;

import com.woowacourse.pickgit.exception.post.PostFormatException;
import java.util.Objects;
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
        validateLengthIsOverThanMaximumContentLength(content);
        this.content = content;
    }

    private void validateLengthIsOverThanMaximumContentLength(String content) {
        if (content.length() > MAXIMUM_CONTENT_LENGTH) {
            throw new PostFormatException();
        }
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostContent that = (PostContent) o;
        return Objects.equals(content, that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
